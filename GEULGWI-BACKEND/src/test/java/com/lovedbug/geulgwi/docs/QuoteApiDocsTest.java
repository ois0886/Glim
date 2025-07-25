package com.lovedbug.geulgwi.docs;

import static io.restassured.RestAssured.given;
import com.lovedbug.geulgwi.entity.Member;
import com.lovedbug.geulgwi.enums.MemberGender;
import com.lovedbug.geulgwi.enums.MemberRole;
import com.lovedbug.geulgwi.enums.MemberStatus;
import com.lovedbug.geulgwi.repository.MemberRepository;
import com.lovedbug.geulgwi.utils.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovedbug.geulgwi.dto.ImageMetaData;
import com.lovedbug.geulgwi.dto.request.BookCreateDto;
import com.lovedbug.geulgwi.dto.request.QuoteCreateDto;
import com.lovedbug.geulgwi.entity.Book;
import com.lovedbug.geulgwi.entity.Quote;
import com.lovedbug.geulgwi.external.image.ImageHandler;
import com.lovedbug.geulgwi.repository.BookRepository;
import com.lovedbug.geulgwi.repository.QuoteRepository;


class QuoteApiDocsTest extends RestDocsTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private ImageHandler imageHandler;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE quote");
        jdbcTemplate.execute("TRUNCATE TABLE book");
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        entityManager.clear();
    }

    @DisplayName("특정 도서의 isbn으로 관련있는 글귀 목록을 조회한다")
    @Test
    void get_public_quotes_by_isbn(){
        Book book = createBook();
        bookRepository.save(book);

        Quote quote1 = Quote.builder()
            .content("첫번째 글귀")
            .views(5)
            .page(10)
            .book(book)
            .memberId(42L)
            .build();

        Quote quote2 = Quote.builder()
            .content("두번째 글귀")
            .views(3)
            .page(20)
            .book(book)
            .memberId(42L)
            .build();

        quoteRepository.saveAll(List.of(quote1, quote2));

        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                pathParameters(
                    parameterWithName("isbn").description("조회할 도서의 isbn")
                ),
                responseFields(
                    fieldWithPath("[].quoteId").description("글귀 ID"),
                    fieldWithPath("[].content").description("글귀 내용"),
                    fieldWithPath("[].views").description("조회수"),
                    fieldWithPath("[].page").description("도서 내 페이지 번호")
                )
                ))
            .when()
            .get("/api/v1/quotes/{isbn}", book.getIsbn())
            .then()
            .statusCode(200);
    }

    @DisplayName("특정 조건으로 정렬된 글귀 목록을 조회한다")
    @Test
    void get_quotes_order_by_condition() {
        Book book = createBook();
        bookRepository.save(book);

        Quote quote = Quote.builder()
            .imagePath("/root")
            .imageName("image.jpg")
            .views(10)
            .memberId(1L)
            .book(book)
            .build();

        quoteRepository.save(quote);

        given(this.spec)
            .param("page", 0)
            .param("size", 10)
            .param("sort", "views,desc")
            .filter(document("{class_name}/{method_name}",
                queryParameters(
                    parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값 0)"),
                    parameterWithName("size").description("페이지 크기 (기본값 10)"),
                    parameterWithName("sort").description("정렬 기준, ex) views,desc 또는 createdDate,asc")
                ),
                responseFields(
                    fieldWithPath("[]").description("글귀 목록"),
                    fieldWithPath("[].quoteId").description("글귀 ID"),
                    fieldWithPath("[].quoteImageName").description("글귀 이미지 이름"),
                    fieldWithPath("[].quoteViews").description("글귀 조회수"),
                    fieldWithPath("[].page").description("글귀에 해당하는 페이지"),
                    fieldWithPath("[].bookId").description("책 ID"),
                    fieldWithPath("[].bookTitle").description("책 제목"),
                    fieldWithPath("[].author").description("책 저자"),
                    fieldWithPath("[].publisher").description("출판사"),
                    fieldWithPath("[].bookCoverUrl").description("책 표지 URL")
                )
            ))
            .when()
            .get("/api/v1/quotes")
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("글귀를 생성한다")
    @Test
    void create_quote() throws Exception {

        Member member = Member.builder()
            .email("hong@naver.com")
            .nickname("홍홍홍")
            .password("pass")
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .gender(MemberGender.MALE)
            .build();

        member = memberRepository.save(member);

        String accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getMemberId());

        when(imageHandler.saveImage(any()))
            .thenReturn(new ImageMetaData("/upload/images", "imageName.jpg"));

        given(this.spec)
            .header("Authorization", "Bearer " + accessToken)
            .multiPart("quoteData", "quoteData.json", objectMapper.writeValueAsBytes(createQuoteCreateDto()), "application/json")
            .multiPart("quoteImage", "quote.jpg", "fake-image-content".getBytes(), "image/jpeg")
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .filter(document("{class_name}/{method_name}",
                requestParts(
                    partWithName("quoteData").description("글귀 생성 정보(JSON)"),
                    partWithName("quoteImage").description("글귀 이미지 파일")
                )
            ))
            .when()
            .post("/api/v1/quotes")
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("글귀_조회수를_1_증가시킨다")
    @Test
    void increase_quote_view_count() {
        Book book = createBook();
        bookRepository.save(book);

        Quote quote = Quote.builder()
            .imagePath("/root")
            .imageName("image.jpg")
            .views(10)
            .page(12)
            .memberId(1L)
            .book(book)
            .build();

        quote = quoteRepository.save(quote);

        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                pathParameters(
                    parameterWithName("id").description("조회수를 증가시킬 글귀의 ID")
                )
            ))
            .when()
            .patch("/api/v1/quotes/{id}/views", quote.getQuoteId())
            .then().log().all()
            .statusCode(204);
    }

    static BookCreateDto createBookCreateDto() {
        return BookCreateDto.builder()
            .title("테스트 제목")
            .author("테스트 저자")
            .translator("테스트 번역가")
            .category("테스트 카테고리")
            .categoryId(1)
            .publisher("테스트 출판사")
            .description("테스트 설명")
            .isbn("1234567890")
            .isbn13("123-1234567890")
            .publishedDate(LocalDate.of(2023, 1, 1))
            .coverUrl("http://example.com/cover.jpg")
            .linkUrl("http://example.com/book")
            .build();
    }

    static QuoteCreateDto createQuoteCreateDto() {
        return QuoteCreateDto.builder()
            .memberId(100L)
            .visibility("PUBLIC")
            .content("테스트용 글귀 내용입니다.")
            .isbn("1234567890")
            .bookCreateData(createBookCreateDto())
            .build();
    }

    static Book createBook() {
        return Book.builder()
            .title("제목")
            .author("작가 : 김작가")
            .isbn("1234567890")
            .coverUrl("/aladdin/image.jpg")
            .build();
    }
}
