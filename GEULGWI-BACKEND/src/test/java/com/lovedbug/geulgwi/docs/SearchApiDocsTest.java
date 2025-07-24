package com.lovedbug.geulgwi.docs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class SearchApiDocsTest extends RestDocsTestSupport {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE curation_item_quote");
        jdbcTemplate.execute("TRUNCATE TABLE curation_item_book");
        jdbcTemplate.execute("TRUNCATE TABLE curation_item");
        jdbcTemplate.execute("TRUNCATE TABLE main_curation");
        jdbcTemplate.execute("TRUNCATE TABLE quote");
        jdbcTemplate.execute("TRUNCATE TABLE book");
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        entityManager.clear();
    }

    @DisplayName("인기_검색어를_조회한다")
    @Test
    void get_search_popular_history() {
        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                responseFields(
                    fieldWithPath("[].searchHistory").description("인기 검색어 리스트")
                )
            ))
            .when()
            .get("/api/v1/searches/popular")
            .then()
            .statusCode(200);
    }
}
