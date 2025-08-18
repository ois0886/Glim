package com.lovedbug.geulgwi.core.domain.image;

import com.lovedbug.geulgwi.external.gpt.dto.GenerateRequest;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageGenerateController {

    public static final String X_IMAGE_PROMPT = "X-Image-Prompt";
    public static final String X_JOB_ID = "X-Job-ID";

    private final ImageGenerateService imageGenerateService;

    @PostMapping("")
    public ResponseEntity<ByteArrayResource> generate(
        @RequestBody GenerateRequest generateRequest
    ) {
        String prompt = generateRequest.getPrompt().trim();
        if (prompt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prompt is empty.");
        }

        String imgPrompt = imageGenerateService.createImagePrompt(prompt);

        byte[] imgBytes = imageGenerateService.generateImageBytes(imgPrompt);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_JOB_ID, UUID.randomUUID().toString());
        String safePrompt = imgPrompt.replaceAll("[\\r\\n]", " ");
        headers.add(X_IMAGE_PROMPT, safePrompt);

        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(headers)
            .contentType(MediaType.IMAGE_PNG)
            .body(new ByteArrayResource(imgBytes));
    }
}


