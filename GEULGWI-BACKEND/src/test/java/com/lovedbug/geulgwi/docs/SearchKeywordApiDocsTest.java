package com.lovedbug.geulgwi.docs;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import com.lovedbug.geulgwi.config.EmbeddedRedisConfig;

@Import(EmbeddedRedisConfig.class)
class SearchKeywordApiDocsTest extends RestDocsTestSupport {

    @DisplayName("인기_검색어를_조회한다")
    @Test
    void get_search_popular_history() {
        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                responseFields(
                    fieldWithPath("[]").description("인기 검색어 리스트")
                )
            ))
            .when()
            .get("/api/v1/search-keywords/popular")
            .then()
            .statusCode(200);
    }
}
