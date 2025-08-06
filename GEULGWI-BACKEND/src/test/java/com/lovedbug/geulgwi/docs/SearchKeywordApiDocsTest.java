package com.lovedbug.geulgwi.docs;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lovedbug.geulgwi.core.domain.search.SearchKeywordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import com.lovedbug.geulgwi.config.TestRedisConfig;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
class SearchKeywordApiDocsTest extends RestDocsTestSupport {

    @Autowired
    private SearchKeywordService searchKeywordService;

    @MockitoBean
    private FirebaseMessaging firebaseMessaging;

    @DynamicPropertySource
    static void setRedisProps(DynamicPropertyRegistry registry) {
        TestRedisConfig.overrideRedisProps(registry);
    }

    @DisplayName("인기_검색어를_조회한다")
    @Test
    void get_search_popular_history() {
        searchKeywordService.increaseKeywordScore("인기 검색어");

        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                responseFields(
                    fieldWithPath("[]").description("인기 검색어 리스트")
                )
            ))
            .when()
            .get("/api/v1/search-keywords/popular")
            .then().log().all()
            .statusCode(200);
    }
}
