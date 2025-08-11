package com.lovedbug.geulgwi.docs;

import com.google.firebase.messaging.FirebaseMessaging;
import com.lovedbug.geulgwi.core.domain.book.BookRepository;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import com.lovedbug.geulgwi.core.security.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@ActiveProfiles("test")
public class LikeApiDocsTest extends RestDocsTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private FirebaseMessaging firebaseMessaging;

    @PersistenceContext
    private EntityManager entityManager;

    private Member member;
    private Book book;
    private Quote quote;
    private String accessToken;

    @BeforeEach
    void setUp(){
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE member_like_quote");
        jdbcTemplate.execute("TRUNCATE TABLE quote");
        jdbcTemplate.execute("TRUNCATE TABLE book");
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        entityManager.clear();

        member = memberRepository.save(createTestMember());
        book = bookRepository.save(createTestBook());
        quote = quoteRepository.save(createTestQuote(book, member));
        accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getMemberId());
    }

    @DisplayName("사용자가 글귀에 좋아요를 등록한다.")
    @Test
    void like_to_quote(){

        given(this.spec)
            .header(JwtUtil.HEADER_AUTH, JwtUtil.TOKEN_PREFIX + accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .filter(document("{class_name}/{method_name}",
                requestHeaders(
                    headerWithName(JwtUtil.HEADER_AUTH).description("Bearer 액세스 토큰")
                ),
                pathParameters(
                    parameterWithName("quoteId").description("사용자가 좋아요 누른 글귀 id(필수)")
                )
            ))
            .when()
            .post("/api/v1/likes/quotes/{quoteId}", quote.getQuoteId())
            .then()
            .log().all()
            .statusCode(200);
    }

    @DisplayName("사용자가 글귀에 좋아요를 등록한다.")
    @Test
    void unlike_to_quote(){

        given(this.spec)
            .header(JwtUtil.HEADER_AUTH, JwtUtil.TOKEN_PREFIX + accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .filter(document("{class_name}/{method_name}",
                requestHeaders(
                    headerWithName(JwtUtil.HEADER_AUTH).description("Bearer 액세스 토큰")
                ),
                pathParameters(
                    parameterWithName("quoteId").description("사용자가 좋아요 누른 글귀 id(필수)")
                )
            ))
            .when()
            .delete("/api/v1/likes/quotes/{quoteId}", quote.getQuoteId())
            .then()
            .log().all()
            .statusCode(200);
    }

    @DisplayName("사용자가 좋아요한 글귀를 조회한다.")
    @Test
    void get_liked_quotes(){

        given(this.spec)
            .header(JwtUtil.HEADER_AUTH, JwtUtil.TOKEN_PREFIX + accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/api/v1/likes/quotes/{quoteId}", quote.getQuoteId())
            .then()
            .statusCode(200);

        given(this.spec)
            .header(JwtUtil.HEADER_AUTH, JwtUtil.TOKEN_PREFIX + accessToken)
            .filter(document("{class_name}/{method_name}",
                requestHeaders(
                    headerWithName(JwtUtil.HEADER_AUTH).description("Bearer 액세스 토큰")
                ),
                responseFields(
                    fieldWithPath("[].quoteId").description("글귀 ID"),
                    fieldWithPath("[].content").description("글귀 내용"),
                    fieldWithPath("[].bookTitle").description("책 제목"),
                    fieldWithPath("[].views").description("글귀 조회수"),
                    fieldWithPath("[].page").description("글귀가 등장하는 페이지"),
                    fieldWithPath("[].likeCount").description("글귀 좋아요 수"),
                    fieldWithPath("[].liked").description("사용자 좋아요 여부")
                )
            ))
            .when()
            .get("/api/v1/likes/quotes/me")
            .then()
            .log().all()
            .statusCode(200);

    }

    private Member createTestMember() {
        return Member.builder()
            .email("like_test_" + System.currentTimeMillis() + "@example.com")
            .password(passwordEncoder.encode("password123"))
            .nickname("likeTestUser")
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .gender(MemberGender.MALE)
            .build();
    }

    private Book createTestBook() {
        return Book.builder()
            .title("test_title")
            .isbn("1234567890123")
            .author("테스트 저자")
            .build();
    }

    private Quote createTestQuote(Book book, Member member) {
        return Quote.builder()
            .content("테스트 글귀입니다. 아주 감명 깊어요.")
            .bookTitle(book.getTitle())
            .page(100)
            .book(book)
            .memberId(member.getMemberId())
            .build();
    }
}
