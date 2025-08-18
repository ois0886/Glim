package com.lovedbug.geulgwi.external.gpt.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class ChatCompletionResponse {
    private List<Choice> choices;

    @Getter
    public static class Choice {
        private GPTMessage message;
    }

}
