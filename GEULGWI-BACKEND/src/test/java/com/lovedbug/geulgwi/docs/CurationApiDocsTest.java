package com.lovedbug.geulgwi.docs;

import static io.restassured.RestAssured.given;
import com.lovedbug.geulgwi.config.TestRedisConfig;
import com.lovedbug.geulgwi.core.domain.book.BookRankingService;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.book.BookRepository;
import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;
import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItem;
import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItemBook;
import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItemQuote;
import com.lovedbug.geulgwi.core.domain.curation.entity.MainCuration;
import com.lovedbug.geulgwi.core.domain.curation.repository.CurationItemBookRepository;
import com.lovedbug.geulgwi.core.domain.curation.repository.CurationItemQuoteRepository;
import com.lovedbug.geulgwi.core.domain.curation.repository.CurationItemRepository;
import com.lovedbug.geulgwi.core.domain.curation.repository.MainCurationRepository;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.domain.quote.QuoteRankingService;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@ActiveProfiles("test")
class CurationApiDocsTest extends RestDocsTestSupport {

    @Autowired
    private BookRankingService bookRankingService;

    @Autowired
    private QuoteRankingService quoteRankingService;

    @Autowired
    private MainCurationRepository mainCurationRepository;

    @Autowired
    private CurationItemRepository curationItemRepository;

    @Autowired
    private CurationItemBookRepository curationItemBookRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private CurationItemQuoteRepository curationItemQuoteRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @DynamicPropertySource
    static void setRedisProps(DynamicPropertyRegistry registry) {
        TestRedisConfig.overrideRedisProps(registry);
    }

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

    @DisplayName("메인 큐레이션을 조회한다")
    @Test
    void get_main_curation() {
        setUpCurationData();

        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                responseFields(
                    fieldWithPath("[]").description("큐레이션 목록"),
                    fieldWithPath("[].curationItemId").type(JsonFieldType.NUMBER).description("큐레이션 아이템 ID").optional(),
                    fieldWithPath("[].title").description("큐레이션 제목"),
                    fieldWithPath("[].description").description("큐레이션 설명"),
                    fieldWithPath("[].curationType").description("큐레이션 타입 (QUOTE 또는 BOOK)"),
                    fieldWithPath("[].contents").description("큐레이션 콘텐츠 목록"),
                    fieldWithPath("[].contents[].bookId").description("책 ID").optional(),
                    fieldWithPath("[].contents[].bookTitle").description("책 제목"),
                    fieldWithPath("[].contents[].author").description("책 저자"),
                    fieldWithPath("[].contents[].publisher").type(JsonFieldType.STRING).description("출판사").optional(),
                    fieldWithPath("[].contents[].bookCoverUrl").type(JsonFieldType.STRING).description("책 커버 URL").optional(),
                    fieldWithPath("[].contents[].quoteId").type(JsonFieldType.NUMBER).description("글귀 ID").optional(),
                    fieldWithPath("[].contents[].imageName").type(JsonFieldType.STRING).description("글귀 이미지 이름").optional()
                )
            ))
            .when()
            .get("/api/v1/curations/main")
            .then().log().all()
            .statusCode(200)
            .extract().body().asString();
    }

    void setUpCurationData() {
        MainCuration mainCuration = new MainCuration();

        mainCurationRepository.save(mainCuration);

        CurationItem bookCurationItem = CurationItem.builder()
            .title("한강 작가의 도서를 만나봐요")
            .description("한강 작가의 도서 큐레이션입니다")
            .curationType(CurationType.BOOK)
            .mainCurationId(mainCuration.getMainCurationId())
            .build();

        curationItemRepository.save(bookCurationItem);

        Book book = Book.builder()
            .title("채식주의자")
            .description("채식 옴뇸뇸")
            .author("작가")
            .publisher("출판사")
            .coverUrl("/book/cover/url")
            .build();

        book = bookRepository.save(book);

        bookRankingService.updateBookRanking(book);

        CurationItemBook curationItemBook = CurationItemBook.builder()
            .curationItemId(bookCurationItem.getCurationItemId())
            .bookId(book.getBookId())
            .build();

        curationItemBookRepository.save(curationItemBook);

        CurationItem quoteCurationItem = CurationItem.builder()
            .title("김김 작가의 유명 글귀를 만나봐요")
            .description("김김 작가의 글귀 큐레이션입니다")
            .curationType(CurationType.QUOTE)
            .mainCurationId(mainCuration.getMainCurationId())
            .build();

        curationItemRepository.save(quoteCurationItem);

        Member member = Member.builder()
            .email("kim@naver.com")
            .password("pass")
            .nickname("닉네임")
            .gender(MemberGender.MALE)
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .build();

        memberRepository.save(member);

        Quote quote = Quote.builder()
            .imageName("image.jpg")
            .memberId(member.getMemberId())
            .book(book)
            .author("작가")
            .bookTitle("책 제목")
            .build();

        quote = quoteRepository.save(quote);

        quoteRankingService.updateQuoteRanking(quote);

        CurationItemQuote curationItemQuote = CurationItemQuote.builder()
            .curationItemId(quoteCurationItem.getCurationItemId())
            .quoteId(quote.getQuoteId())
            .build();

        curationItemQuoteRepository.save(curationItemQuote);
    }
}
