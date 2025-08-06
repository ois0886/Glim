package com.lovedbug.geulgwi.docs;

import com.google.firebase.messaging.FirebaseMessaging;
import com.lovedbug.geulgwi.core.domain.admin.dto.request.CreateCurationRequest;
import com.lovedbug.geulgwi.core.domain.admin.dto.request.UpdateCurationRequest;
import com.lovedbug.geulgwi.core.domain.book.BookRepository;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
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
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.payload.JsonFieldType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@ActiveProfiles("test")
class AdminApiDocsTest extends RestDocsTestSupport {
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

    @MockitoBean
    private FirebaseMessaging firebaseMessaging;

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
    }

    @DisplayName("관리자 페이지에서 메인 큐레이션을 조회한다")
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
                                fieldWithPath("[].contents[].publisher").description("출판사"),
                                fieldWithPath("[].contents[].bookCoverUrl").type(JsonFieldType.STRING).description("책 커버 URL").optional(),
                                fieldWithPath("[].contents[].quoteId").type(JsonFieldType.NUMBER).description("글귀 ID").optional(),
                                fieldWithPath("[].contents[].imageName").type(JsonFieldType.STRING).description("글귀 이미지 이름").optional()
                        )
                ))
                .when()
                .get("/api/v1/admin/curations/main")
                .then().log().all()
                .statusCode(200)
                .extract().body().asString();
    }

    @DisplayName("관리자 페이지에서 메인 큐레이션을 생성한다")
    @Test
    void post_main_curation() {
        setUpCurationData();
        Long bookId = bookRepository.findAll().getFirst().getBookId();
        CreateCurationRequest requestDto = CreateCurationRequest.builder()
                .name("테스트 큐레이션")
                .description("테스트 설명")
                .curationType(CurationType.BOOK)
                .bookIds(List.of(bookId))
                .quoteIds(Collections.emptyList())
                .build();

        given(this.spec)
                .contentType("application/json")
                .body(requestDto)
                .filter(document("{class_name}/{method_name}",
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("큐레이션 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("큐레이션 설명").optional(),
                                fieldWithPath("curationType").type(JsonFieldType.STRING).description("큐레이션 타입 (BOOK 또는 QUOTE)"),
                                fieldWithPath("bookIds[]").type(JsonFieldType.ARRAY).description("큐레이션할 책 IDs"),
                                fieldWithPath("quoteIds[]").type(JsonFieldType.ARRAY).description("큐레이션할 글귀 IDs")
                        ),
                        responseFields(
                                fieldWithPath("mainCurationId").type(JsonFieldType.NUMBER).description("메인 큐레이션 ID")
                        )
                ))
                .when()
                .post("/api/v1/admin/curations")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("관리자 페이지에서 큐레이션 아이템을 수정한다")
    @Test
    void put_update_curation_item() {
        setUpCurationData();
        Long itemId = curationItemRepository.findAll().get(0).getCurationItemId();
        Long bookId = bookRepository.findAll().get(0).getBookId();

        UpdateCurationRequest requestDto = UpdateCurationRequest.builder()
                .name("업데이트된 제목")
                .description("업데이트된 설명")
                .curationType(CurationType.BOOK)
                .bookIds(List.of(bookId))
                .quoteIds(Collections.emptyList())
                .build();

        given(this.spec)
                .contentType("application/json")
                .body(requestDto)
                .filter(document("{class_name}/{method_name}",
                        pathParameters(
                                parameterWithName("itemId").description("큐레이션 아이템 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("큐레이션 아이템 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("큐레이션 아이템 설명").optional(),
                                fieldWithPath("curationType").type(JsonFieldType.STRING).description("큐레이션 타입 (BOOK 또는 QUOTE)"),
                                fieldWithPath("bookIds[]").type(JsonFieldType.ARRAY).description("BOOK 타입일 때 연관할 책 IDs"),
                                fieldWithPath("quoteIds[]").type(JsonFieldType.ARRAY).description("QUOTE 타입일 때 연관할 글귀 IDs")
                        )
                ))
                .when()
                .put("/api/v1/admin/curations/items/{itemId}", itemId)
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("관리자 페이지에서 큐레이션 아이템을 삭제한다")
    @Test
    void delete_curation_item() {
        setUpCurationData();
        Long itemId = curationItemRepository.findAll().get(0).getCurationItemId();

        given(this.spec)
                .filter(document("{class_name}/{method_name}",
                            pathParameters(
                                    parameterWithName("itemId").description("큐레이션 아이템 ID")
                            )
                ))
                .when()
                .delete("/api/v1/admin/curations/items/{itemId}", itemId)
                .then().log().all()
                .statusCode(204);
    }

    void setUpCurationData() {
        MainCuration mainCuration = new MainCuration();

        mainCurationRepository.save(mainCuration);

        CurationItem bookCurationItem = CurationItem.builder()
                .title("박승준 작가의 도서를 만나봐요")
                .description("박승준 작가의 도서 큐레이션입니다")
                .curationType(CurationType.BOOK)
                .mainCurationId(mainCuration.getMainCurationId())
                .build();

        curationItemRepository.save(bookCurationItem);

        Book book = Book.builder()
                .title("채식주의자")
                .description("채식 옴뇸뇸")
                .author("박승준")
                .publisher("출판사")
                .coverUrl("/book/cover/url")
                .build();

        bookRepository.save(book);

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
                .build();

        quoteRepository.save(quote);

        CurationItemQuote curationItemQuote = CurationItemQuote.builder()
                .curationItemId(quoteCurationItem.getCurationItemId())
                .quoteId(quote.getQuoteId())
                .build();

        curationItemQuoteRepository.save(curationItemQuote);
    }
}
