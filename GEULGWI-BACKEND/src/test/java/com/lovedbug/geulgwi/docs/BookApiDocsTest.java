package com.lovedbug.geulgwi.docs;

import static io.restassured.RestAssured.given;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.payload.FieldDescriptor;
import com.lovedbug.geulgwi.entity.Book;
import com.lovedbug.geulgwi.repository.BookRepository;

public class BookApiDocsTest extends RestDocsTestSupport {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE book");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        entityManager.clear();
    }

    @DisplayName("키워드를_통해_도서를_검색한다")
    @Test
    void get_books_by_keyword() {
        given(this.spec)
                .param("keyword", "abc")
                .param("page", "1")
                .param("searchQueryType", "KEYWORD")
                .filter(document("{class_name}/{method_name}",
                        queryParameters(
                                parameterWithName("keyword").description("검색 키워드 (필수)"),
                                parameterWithName("page").description("페이지 번호 (선택, 기본값 1)"),
                                parameterWithName("searchQueryType").description("검색 타입 (선택, KEYWORD(기본값), TITLE, AUTHOR, PUBLISHER)")
                        ),
                        responseFields(bookItemFields())
                ))
                .when()
                .get("/api/v1/books")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("인기있는_도서_목록을_검색한다")
    @Test
    void get_popular_books() {
        given(this.spec)
                .param("page", "1")
                .param("searchQueryType", "BESTSELLER")
                .filter(document("{class_name}/{method_name}",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (선택, 기본값 1)"),
                                parameterWithName("searchQueryType").description("검색 타입 (선택, BESTSELLER(기본값), ITEM_NEW_ALL, ITEM_NEW_SPECIAL, ITEM_EDITOR_CHOICE, BLOG_BEST)")
                        ),
                        responseFields(bookItemFields())
                ))
                .when()
                .get("/api/v1/books/popular")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("키워드를_통해_인기_검색어를_검색한다")
    @Test
    void get_search_keyword_history() {
        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                pathParameters(
                    parameterWithName("keyword").description("검색 키워드 (선택, 경로 변수)")
                ),
                responseFields(
                    fieldWithPath("[].author1").description("1위 검색어"),
                    fieldWithPath("[].author2").description("2위 검색어"),
                    fieldWithPath("[].author3").description("3위 검색어"),
                    fieldWithPath("[].author4").description("4위 검색어"),
                    fieldWithPath("[].author5").description("5위 검색어"),
                    fieldWithPath("[].author6").description("6위 검색어"),
                    fieldWithPath("[].author7").description("7위 검색어"),
                    fieldWithPath("[].author8").description("8위 검색어"),
                    fieldWithPath("[].author9").description("9위 검색어"),
                    fieldWithPath("[].author10").description("10위 검색어")
                )
            ))
            .when()
            .get("/api/v1/books/search/{keyword}", "작가")
            .then()
            .statusCode(200);
    }

    @DisplayName("도서_조회수를_1_증가시킨다")
    @Test
    void increase_book_view_count() {
        Book book = bookRepository.save(Book.builder().title("title").build());

        given(this.spec)
                .filter(document("{class_name}/{method_name}",
                        pathParameters(
                                parameterWithName("id").description("조회수를 증가시킬 도서의 ID")
                        )
                ))
                .when()
                .patch("/api/v1/books/{id}/views", book.getBookId())
                .then().log().all()
                .statusCode(204);
    }

    public static List<FieldDescriptor> bookItemFields() {
        return List.of(
                fieldWithPath("[].title").description("책 제목"),
                fieldWithPath("[].linkUrl").description("책 상세 링크"),
                fieldWithPath("[].author").description("저자"),
                fieldWithPath("[].publishedDate").description("출판일"),
                fieldWithPath("[].description").description("책 설명"),
                fieldWithPath("[].isbn").description("ISBN (10자리)"),
                fieldWithPath("[].isbn13").description("ISBN (13자리)"),
                fieldWithPath("[].itemId").description("알라딘 아이템 ID"),
                fieldWithPath("[].priceSales").description("판매가"),
                fieldWithPath("[].priceStandard").description("정가"),
                fieldWithPath("[].mallType").description("상품 유형"),
                fieldWithPath("[].stockStatus").description("재고 상태"),
                fieldWithPath("[].mileage").description("마일리지"),
                fieldWithPath("[].coverUrl").description("표지 이미지 URL"),
                fieldWithPath("[].categoryId").description("카테고리 ID"),
                fieldWithPath("[].categoryName").description("카테고리 이름"),
                fieldWithPath("[].publisher").description("출판사"),
                fieldWithPath("[].translator").description("번역자"),
                fieldWithPath("[].salesPoint").description("판매 지수"),
                fieldWithPath("[].adult").description("성인 도서 여부"),
                fieldWithPath("[].fixedPrice").description("정가제 여부"),
                fieldWithPath("[].customerReviewRank").description("고객 리뷰 평점")
        );
    }
}
