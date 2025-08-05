package com.lovedbug.geulgwi.docs;

import com.lovedbug.geulgwi.core.domain.book.BookRepository;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.domain.quote.QuoteRepository;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.payload.JsonFieldType;
import java.time.LocalDate;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class AdminSearchApiDocsTest extends RestDocsTestSupport{

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE curation_item_quote RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE curation_item_book RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE curation_item RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE main_curation RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE quote RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE book RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE member RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        entityManager.clear();

        setUpSearchData();
    }
    private void setUpSearchData() {
        Book b1 = Book.builder()
            .title("Java Programming")
            .author("Alice")
            .category(null)
            .publisher("TechPress")
            .description("Learn Java")
            .isbn("1111111111")
            .isbn13("111-1111111111")
            .publishedDate(LocalDate.of(2020,1,1))
            .coverUrl("/covers/java.jpg")
            .linkUrl("/books/java")
            .views(100)
            .build();
        Book b2 = Book.builder()
            .title("Spring Boot Guide")
            .author("Bob")
            .category(null)
            .publisher("SpringInc")
            .description("Spring Boot 실전")
            .isbn("2222222222")
            .isbn13("222-2222222222")
            .publishedDate(LocalDate.of(2021,2,2))
            .coverUrl("/covers/spring.jpg")
            .linkUrl("/books/spring")
            .views(50)
            .build();
        bookRepository.saveAll(List.of(b1, b2));


        Member m = Member.builder()
            .email("user@example.com")
            .password("pass")
            .nickname("user")
            .gender(MemberGender.FEMALE)
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .build();
        memberRepository.save(m);

        Quote q1 = Quote.builder()
            .content("인생은 아름답다")
            .imageName("life.jpg")
            .memberId(m.getMemberId())
            .book(b1)
            .bookTitle("힘들다")
            .build();
        quoteRepository.save(q1);
    }

    @DisplayName("관리자 페이지에서 도서를 키워드로 검색한다")
    @Test
    void search_books() {
        given(this.spec)
            .param("keyword", "java")
            .param("page", 0)
            .param("size", 10)
            .param("sort", "views,desc")
            .filter(document("{class_name}/{method_name}",
                queryParameters(
                    parameterWithName("keyword").description("검색어"),
                    parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값 0)"),
                    parameterWithName("size").description("페이지 크기 (기본값 10)"),
                    parameterWithName("sort").description("정렬 기준, ex) views,desc 또는 createdDate,asc")
                ),
                responseFields(
                    fieldWithPath("[].bookId").type(JsonFieldType.NUMBER).description("도서 ID"),
                    fieldWithPath("[].title").type(JsonFieldType.STRING).description("도서 제목"),
                    fieldWithPath("[].author").type(JsonFieldType.STRING).description("저자"),
                    fieldWithPath("[].categoryName").type(JsonFieldType.STRING).description("카테고리 이름").optional(),
                    fieldWithPath("[].categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID").optional(),
                    fieldWithPath("[].publisher").type(JsonFieldType.STRING).description("출판사"),
                    fieldWithPath("[].description").type(JsonFieldType.STRING).description("설명"),
                    fieldWithPath("[].isbn").type(JsonFieldType.STRING).description("ISBN"),
                    fieldWithPath("[].isbn13").type(JsonFieldType.STRING).description("ISBN-13"),
                    fieldWithPath("[].publishedDate").type(JsonFieldType.STRING).description("출판일"),
                    fieldWithPath("[].coverUrl").type(JsonFieldType.STRING).description("커버 이미지 URL"),
                    fieldWithPath("[].linkUrl").type(JsonFieldType.STRING).description("도서 링크 URL"),
                    fieldWithPath("[].views").type(JsonFieldType.NUMBER).description("조회수")
                )
            ))
            .when()
            .get("/api/v1/admin/search/book")
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("관리자 페이지에서 글귀를 키워드로 검색한다")
    @Test
    void search_quotes() {
        given(this.spec)
            .param("keyword", "인생은")
            .param("page", 0)
            .param("size", 10)
            .param("sort", "views,desc")
            .filter(document("{class_name}/{method_name}",
                queryParameters(
                    parameterWithName("keyword").description("검색어"),
                    parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값 0)"),
                    parameterWithName("size").description("페이지 크기 (기본값 10)"),
                    parameterWithName("sort").description("정렬 기준, ex) views,desc 또는 createdDate,asc")
                ),
                responseFields(
                    fieldWithPath("[].quoteId").type(JsonFieldType.NUMBER).description("글귀 ID"),
                    fieldWithPath("[].content").type(JsonFieldType.STRING).description("글귀 내용"),
                    fieldWithPath("[].quoteImage").type(JsonFieldType.STRING).description("이미지 URL"),
                    fieldWithPath("[].views").type(JsonFieldType.NUMBER).description("글귀 조회수"),
                    fieldWithPath("[].page").type(JsonFieldType.NUMBER).description("글귀 페이지"),
                    fieldWithPath("[].bookTitle").type(JsonFieldType.STRING).description("도서 제목")
                )
            ))
            .when()
            .get("/api/v1/admin/search/quote")
            .then().log().all()
            .statusCode(200);
    }
}
