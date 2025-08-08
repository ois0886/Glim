package com.lovedbug.geulgwi.docs;

import com.lovedbug.geulgwi.core.domain.image.ImageGenerateService;
import com.lovedbug.geulgwi.external.gpt.dto.GenerateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@ActiveProfiles("test")
public class OpenAiControllerDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private ImageGenerateService imageGenerateService;

    @DisplayName("글귀에 어울리는 이미지를 GPT가 생성한다")
    @Test
    void generate_image() {
        String userPrompt = "인생은 아름답다";
        String imgPrompt  = "A beautiful watercolor of sunrise over mountains";
        byte[] imageBytes = "fakepngbytes".getBytes();

        org.mockito.BDDMockito.given(imageGenerateService.createImagePrompt(userPrompt)).willReturn(imgPrompt);
        org.mockito.BDDMockito.given(imageGenerateService.generateImageBytes(imgPrompt)).willReturn(imageBytes);

        GenerateRequest reqDto = GenerateRequest.builder()
            .prompt(userPrompt)
            .build();

        byte[] returned =
            given(this.spec)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reqDto)
                .filter(document("{class_name}/{method_name}",
                    requestFields(
                        fieldWithPath("prompt").description("이미지 생성을 위한 원본 텍스트")
                    ),
                    responseHeaders(
                        headerWithName("X-Job-ID").description("생성 작업 ID").optional(),
                        headerWithName("X-Image-Prompt").description("DALL·E 에 넘겨진 최종 프롬프트").optional()
                    )
                ))
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(201)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .header("X-Image-Prompt", imgPrompt)
                .extract()
                .response()
                .asByteArray();

        org.assertj.core.api.Assertions.assertThat(returned).hasSize(imageBytes.length);
    }
}
