package com.lovedbug.geulgwi.external.gpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("response_format")
    private String responseFormat;
}
