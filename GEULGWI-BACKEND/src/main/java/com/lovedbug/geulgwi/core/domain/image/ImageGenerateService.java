package com.lovedbug.geulgwi.core.domain.image;

import com.lovedbug.geulgwi.external.gpt.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageGenerateService {
    private final WebClient openAiWebClient;

    public String createImagePrompt(String userText) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-4.1")
                .temperature(0.7)
                .max_tokens(50)
                .messages(List.of(
                        GPTMessage.builder()
                                .role("system")
                                .content("당신은 DALL·E 같은 이미지 생성 AI에게 넘길 '이미지 생성 프롬프트'를 만드는 전문가입니다.")
                                .build(),
                        GPTMessage.builder()
                                .role("user")
                                .content("다음 문구를 바탕으로, 사용자가 잠금화면으로 사용할 수 있는 텍스트가 포함되어 있지 않은 풍경 사진 이미지 생성 프롬프트를 영어 한 문장으로 만들어 주세요:\n\n\""
                                        + userText + "\"")
                                .build()
                ))
                .build();
        ChatCompletionResponse chatCompletionResponse = openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(chatCompletionRequest)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .block();

        if (chatCompletionResponse == null || chatCompletionResponse.getChoices().isEmpty()) {
            throw new IllegalStateException("Chat completion returned no choices");
        }
        return chatCompletionResponse.getChoices().getFirst().getMessage().getContent().trim();
    }

    public byte[] generateImageBytes(String imgPrompt) {

        ImageGenerationRequest imageGenerationRequest = ImageGenerationRequest.builder()
                .model("dall-e-3")
                .prompt(imgPrompt)
                .size("1024x1792")
                .responseFormat("b64_json")
                .build();

        ImageGenerationResponse imageGenerationResponse = openAiWebClient.post()
                .uri("/images/generations")
                .bodyValue(imageGenerationRequest)
                .retrieve()
                .bodyToMono(ImageGenerationResponse.class)
                .block();

        if (imageGenerationResponse == null || imageGenerationResponse.getData().isEmpty()) {
            throw new IllegalStateException("Image generation returned no data");
        }
        String b64 = imageGenerationResponse.getData().getFirst().getB64Json();
        if (b64 == null) {
            throw new IllegalStateException("No b64_json in response");
        }

        return Base64.getDecoder().decode(b64);
    }
}
