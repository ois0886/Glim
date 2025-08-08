package com.lovedbug.geulgwi.external.gpt.dto;

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
        private String b64_json;
    }
}
