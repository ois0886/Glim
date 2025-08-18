package com.lovedbug.geulgwi.external.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GPTMessage {
    private String role;
    private String content;
}
