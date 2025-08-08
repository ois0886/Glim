package com.lovedbug.geulgwi.external.gpt.dto;

import lombok.*;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatCompletionRequest {
    private String model;
    private List<Message> messages;
    private double temperature;
    private int max_tokens;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
}
