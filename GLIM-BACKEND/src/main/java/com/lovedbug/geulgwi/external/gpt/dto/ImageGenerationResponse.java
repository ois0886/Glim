package com.lovedbug.geulgwi.external.gpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Data
public class ImageGenerationResponse {
    private List<Data> data;

    @Getter
    public static class Data {
        private String url;

        @JsonProperty("b64_json")
        private String b64Json;
    }
}
