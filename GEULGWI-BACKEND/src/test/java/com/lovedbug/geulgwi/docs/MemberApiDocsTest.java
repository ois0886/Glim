package com.lovedbug.geulgwi.docs;

import com.lovedbug.geulgwi.core.domain.member.dto.SignUpRequestDto;
import com.lovedbug.geulgwi.core.domain.member.dto.UpdateRequestDto;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.external.email.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.time.LocalDateTime;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.mockito.ArgumentMatchers.any;

public class MemberApiDocsTest extends RestDocsTestSupport{

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clearDatabase() {

        memberRepository.deleteAllInBatch();
    }

    @MockitoBean
    private EmailSender emailSender;

    @DisplayName("사용자가_사용할_계정을_생성한다")
    @Test
    void create_member(){

        doNothing().when(emailSender).sendWelcomeEmail(any(String.class), any(String.class));

        Member testMember = TestMemberFactory.createGetTestMember(passwordEncoder);

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
            .email(testMember.getEmail())
            .password("pwd1234")
            .nickname("testNickname1")
            .birthDate(LocalDateTime.of(1999, 1, 7, 0,0,0))
            .gender(MemberGender.MALE)
            .build();

        given(this.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(signUpRequestDto)
            .filter(document("{class_name}/{method_name}",
                    requestFields(
                        fieldWithPath("email").description("로그인시 사용할 id(필수)"),
                        fieldWithPath("password").description("로그인시 사용할 pwd(필수)"),
                        fieldWithPath("nickname").description("사용자가 사용할 nickname(필수)"),
                        fieldWithPath("birthDate").description("생년월일 (선택, 기본값 CURRENT_TIMESTAMP)"),
                        fieldWithPath("gender").description("성별 (선택, 기본값 MALE)")
                    ),
                    responseFields(
                        fieldWithPath("email").description("가입한 사용자 이메일"),
                        fieldWithPath("nickname").description("가입한 사용자 닉네임"),
                        fieldWithPath("message").description("회원가입 처리 결과 메시지")
                    )
                ))
            .when()
            .post("/api/v1/members")
            .then().log().all()
            .statusCode(201);
    }

    @DisplayName("사용자_id를_통해_조회한다.")
    @Test
    void get_member_by_id(){

        Member testMember = TestMemberFactory.createVerifiedTestMember(passwordEncoder);
        Member savedMember = memberRepository.save(testMember);

        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                responseFields(memberItemFields())
            ))
            .when()
            .get("/api/v1/members/{memberId}",savedMember.getMemberId())
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("사용자_정보를_수정한다")
    @Test
    void update_member() {

        Member testMember = TestMemberFactory.createVerifiedTestMember(passwordEncoder);
        Member savedMember = memberRepository.save(testMember);

        UpdateRequestDto updateRequestDto = UpdateRequestDto.builder()
            .password("updatePwd123")
            .nickname("updatedNickname")
            .birthDate(LocalDateTime.of(1999, 1, 7, 0,0,0))
            .gender(MemberGender.MALE)
            .build();

        given(this.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(updateRequestDto)
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
            .put("/api/v1/members/{memberId}", savedMember.getMemberId())
            .then().log().all()
            .statusCode(200);
    }

    @DisplayName("회원을_삭제한다(논리삭제)")
    @Test
    void soft_delete_member() {

        Member testMember = TestMemberFactory.createVerifiedTestMember(passwordEncoder);
        Member savedMember = memberRepository.save(testMember);

        given(this.spec)
            .filter(document("{class_name}/{method_name}",
                pathParameters(
                    parameterWithName("memberId").description("삭제할 회원의 ID")
                ),
                responseFields(softDeleteResponseFields())
            ))
            .when()
            .patch("/api/v1/members/{memberId}/status", savedMember.getMemberId())
            .then().log().all()
            .statusCode(200);
    }

    public static List<FieldDescriptor> memberItemFields() {

        return List.of(
            fieldWithPath("memberId").description("사용자 고유 ID"),
            fieldWithPath("email").description("사용자 이메일"),
            fieldWithPath("password").description("사용자 비밀번호"),
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
            fieldWithPath("password").description("회원 비밀번호"),
            fieldWithPath("nickname").description("회원 닉네임"),
            fieldWithPath("status").description("회원 상태 (INACTIVE로 변경됨)"),
            fieldWithPath("birthDate").description("회원 생년월일"),
            fieldWithPath("gender").description("회원 성별")
        );
    }

    public static class TestMemberFactory {

        public static Member.MemberBuilder memberBuilder(PasswordEncoder passwordEncoder) {

            return Member.builder()
                .password(passwordEncoder.encode("pwd1234"))
                .nickname("testNickname")
                .birthDate(LocalDateTime.of(1999, 1, 7, 0,0,0))
                .gender(MemberGender.MALE);
        }

        public static Member createMember(String emailPrefix, PasswordEncoder passwordEncoder) {

            String uniqueEmail = emailPrefix + "_" + System.currentTimeMillis() + "@example.com";

            return memberBuilder(passwordEncoder)
                .email(uniqueEmail)
                .build();
        }

        public static Member createVerifiedMember(String emailPrefix, PasswordEncoder passwordEncoder) {

            String uniqueEmail = emailPrefix + "_" + System.currentTimeMillis() + "@example.com";

            return memberBuilder(passwordEncoder)
                .email(uniqueEmail)
                .build();
        }

        public static Member createGetTestMember(PasswordEncoder passwordEncoder) {

            return createMember("test_get", passwordEncoder);
        }

        public static Member createVerifiedTestMember(PasswordEncoder passwordEncoder) {

            return createVerifiedMember("test_verified", passwordEncoder);
        }

        public static Member[] createGetAllVerifiedTestMembers(int size, PasswordEncoder passwordEncoder) {

            Member[] members = new Member[size];

            for (int i = 0; i < size; i++){
                members[i] = createVerifiedMember("test_all_verified_" + (i + 1), passwordEncoder);
            }

            return members;
        }
    }
}
