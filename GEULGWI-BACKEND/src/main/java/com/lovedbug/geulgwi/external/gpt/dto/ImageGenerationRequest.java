package com.lovedbug.geulgwi.external.gpt.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class ImageGenerationRequest {
    private String model;
    private String prompt;
    private String size;
    private String response_format;
}
