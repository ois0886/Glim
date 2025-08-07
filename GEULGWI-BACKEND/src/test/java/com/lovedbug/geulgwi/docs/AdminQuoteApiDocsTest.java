package com.lovedbug.geulgwi.docs;

import com.lovedbug.geulgwi.core.domain.book.BookRepository;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@ActiveProfiles("test")
public class AdminQuoteApiDocsTest extends RestDocsTestSupport {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearAndSetup() {
        // 테이블 비우기 & 시퀀스 리셋
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE quote RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE book RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE member RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        entityManager.clear();

        Member m = Member.builder()
            .email("user@example.com")
            .password("pass")
            .nickname("testUser")
            .gender(MemberGender.MALE)
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .build();
        memberRepository.save(m);

        Book b = Book.builder()
            .title("Test Book")
            .author("Author")
            .publisher("Pub")
            .coverUrl("/cover.jpg")
            .description("Desc")
            .isbn("111")
            .isbn13("111-1111111111")
            .publishedDate(LocalDate.of(2020,1,1))
            .linkUrl("/link")
            .build();
        bookRepository.save(b);

        Quote q1 = Quote.builder()
            .content("Life is beautiful")
            .imageName("life.jpg")
            .views(5)
            .page(10)
            .memberId(m.getMemberId())
            .book(b)
            .build();
        Quote q2 = Quote.builder()
            .content("Live and let live")
            .imageName("live.jpg")
            .views(7)
            .page(20)
            .memberId(m.getMemberId())
            .book(b)
            .build();
        quoteRepository.saveAll(List.of(q1, q2));
    }

    @DisplayName("관리자: 모든 글귀 조회")
    @Test
    void get_all_quotes() {
        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                responseFields(
                    fieldWithPath("[]").description("글귀 목록"),
                    fieldWithPath("[].quoteId").type(JsonFieldType.NUMBER).description("글귀 ID"),
                    fieldWithPath("[].content").type(JsonFieldType.STRING).description("글귀 내용"),
                    fieldWithPath("[].views").type(JsonFieldType.NUMBER).description("조회수"),
                    fieldWithPath("[].page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                    fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("생성 일시").optional()
                )
            ))
            .when()
            .get("/api/v1/admin/quotes")
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("관리자: 글귀 삭제")
    @Test
    void delete_quote() {
        Long quoteId = quoteRepository.findAll().get(0).getQuoteId();

        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                pathParameters(
                    parameterWithName("quoteId").description("삭제할 글귀 ID")
                )
            ))
            .when()
            .delete("/api/v1/admin/quotes/{quoteId}", quoteId)
            .then().log().all()
            .statusCode(200);
    }
}
