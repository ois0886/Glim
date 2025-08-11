package com.lovedbug.geulgwi.docs;


import com.google.firebase.messaging.FirebaseMessaging;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.domain.member.dto.request.UpdateRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@ActiveProfiles("test")
public class AdminMemberApiDocsTest extends RestDocsTestSupport{

    @Autowired
    private MemberRepository memberRepository;

    @MockitoBean
    private FirebaseMessaging firebaseMessaging;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearDatabaseAndSetup() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE member RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        entityManager.clear();

        setUpUsers();
    }

    @DisplayName("관리자: 모든 멤버 조회")
    @Test
    void get_all_members() {
        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                responseFields(
                    fieldWithPath("[]").description("회원 목록"),
                    fieldWithPath("[].memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("[].email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("[].gender").type(JsonFieldType.STRING).description("성별"),
                    fieldWithPath("[].status").type(JsonFieldType.STRING).description("회원 상태"),
                    fieldWithPath("[].birthDate").description("회원 생일")
                )
            ))
            .when()
            .get("/api/v1/admin/members")
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("관리자: 특정 활성 회원 조회")
    @Test
    void get_active_member_by_id() {
        Long memberId = memberRepository.findAll().stream()
            .filter(m -> m.getStatus() == MemberStatus.ACTIVE)
            .findFirst()
            .get()
            .getMemberId();

        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                pathParameters(
                    parameterWithName("memberId").description("조회할 회원 ID")
                ),
                responseFields(
                    fieldWithPath("memberId").description("사용자 고유 ID"),
                    fieldWithPath("email").description("사용자 이메일"),
                    fieldWithPath("nickname").description("사용자 닉네임"),
                    fieldWithPath("status").description("회원 상태 (ACTIVE, INACTIVE)"),
                    fieldWithPath("birthDate").description("사용자 생년월일"),
                    fieldWithPath("gender").description("사용자 성별 (MALE, FEMALE)")
                )
            ))
            .when()
            .get("/api/v1/admin/members/active/{memberId}", memberId)
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("관리자: 모든 활성 회원 조회")
    @Test
    void get_all_active_members() {
        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                responseFields(
                    fieldWithPath("[]").description("회원 목록"),
                    fieldWithPath("[].memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("[].email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("[].gender").type(JsonFieldType.STRING).description("성별"),
                    fieldWithPath("[].status").type(JsonFieldType.STRING).description("회원 상태"),
                    fieldWithPath("[].birthDate").description("회원 생일")
                )
            ))
            .when()
            .get("/api/v1/admin/members/active")
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("관리자: 회원 정보 수정")
    @Test
    void update_member() {
        Long memberId = memberRepository.findAll().get(0).getMemberId();
        UpdateRequest updateDto = UpdateRequest.builder()
            .nickname("updatedNick")
            .build();

        given(this.spec)
            .contentType("application/json")
            .body(updateDto)
            .filter(document("{class_name}/{method_name}",
                pathParameters(
                    parameterWithName("memberId").description("수정할 사용자의 ID")
                ),
                requestFields(
                    fieldWithPath("password").description("수정할 비밀번호(선택)"),
                    fieldWithPath("nickname").description("수정할 닉네임 (선택)"),
                    fieldWithPath("birthDate").description("수정할 생년월일 (선택)"),
                    fieldWithPath("gender").description("수정할 성별 (선택)")
                ),
                responseFields(memberItemFields())
            ))
            .when()
            .put("/api/v1/admin/members/{memberId}", memberId)
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("관리자: 회원 논리삭제 (상태 변경)")
    @Test
    void soft_delete_member() {
        Long memberId = memberRepository.findAll().get(0).getMemberId();

        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                pathParameters(
                    parameterWithName("memberId").description("삭제할 회원의 ID")
                ),
                responseFields(softDeleteResponseFields())
            ))
            .when()
            .patch("/api/v1/admin/members/{memberId}/status", memberId)
            .then().log().all()
            .statusCode(200);
    }

    public static List<FieldDescriptor> memberItemFields() {

        return List.of(
            fieldWithPath("memberId").description("사용자 고유 ID"),
            fieldWithPath("email").description("사용자 이메일"),
            fieldWithPath("nickname").description("사용자 닉네임"),
            fieldWithPath("status").description("회원 상태 (ACTIVE, INACTIVE)"),
            fieldWithPath("birthDate").description("사용자 생년월일"),
            fieldWithPath("gender").description("사용자 성별 (MALE, FEMALE)")
        );
    }

    public static List<FieldDescriptor> softDeleteResponseFields() {

        return List.of(
            fieldWithPath("memberId").description("회원 고유 ID"),
            fieldWithPath("email").description("회원 이메일"),
            fieldWithPath("nickname").description("회원 닉네임"),
            fieldWithPath("status").description("회원 상태 (INACTIVE로 변경됨)"),
            fieldWithPath("birthDate").description("회원 생년월일"),
            fieldWithPath("gender").description("회원 성별")
        );
    }

    private void setUpUsers() {
        Member active1 = Member.builder()
            .email("active1@example.com")
            .password("pass")
            .nickname("activeUser1")
            .gender(MemberGender.MALE)
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .build();

        Member active2 = Member.builder()
            .email("active2@example.com")
            .password("pass")
            .nickname("activeUser2")
            .gender(MemberGender.FEMALE)
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .build();

        Member inactive = Member.builder()
            .email("inactive@example.com")
            .password("pass")
            .nickname("inactiveUser")
            .gender(MemberGender.MALE)
            .status(MemberStatus.INACTIVE)
            .role(MemberRole.USER)
            .build();

        memberRepository.saveAll(List.of(active1, active2, inactive));
    }
}
