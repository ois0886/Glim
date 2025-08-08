package com.lovedbug.geulgwi.external.gpt;

import com.lovedbug.geulgwi.external.gpt.dto.GenerateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageGenerateController {

    private final OpenAIService openAIService;

    @PostMapping("")
    public ResponseEntity<ByteArrayResource> generate(
        @RequestBody GenerateRequest generateRequest
    ) {
        String prompt = generateRequest.getPrompt().trim();
        if (prompt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prompt is empty.");
        }

        String imgPrompt = openAIService.createImagePrompt(prompt);
        log.info("Image prompt created: {}", imgPrompt);
        byte[] imgBytes = openAIService.generateImageBytes(imgPrompt);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Job-ID", UUID.randomUUID().toString());
        String safePrompt = imgPrompt.replaceAll("[\\r\\n]", " ");
        headers.add("X-Image-Prompt", safePrompt);

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.IMAGE_PNG)
            .body(new ByteArrayResource(imgBytes));
    }
}


