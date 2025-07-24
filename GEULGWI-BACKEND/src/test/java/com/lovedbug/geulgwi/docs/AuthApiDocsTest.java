package com.lovedbug.geulgwi.docs;

import com.lovedbug.geulgwi.config.SecurityConstants;
import com.lovedbug.geulgwi.dto.request.EmailVerificationRequestDto;
import com.lovedbug.geulgwi.dto.request.LoginRequestDto;
import com.lovedbug.geulgwi.entity.Member;
import com.lovedbug.geulgwi.enums.MemberGender;
import com.lovedbug.geulgwi.enums.MemberRole;
import com.lovedbug.geulgwi.enums.MemberStatus;
import com.lovedbug.geulgwi.repository.MemberRepository;
import com.lovedbug.geulgwi.service.EmailVerificationService;
import com.lovedbug.geulgwi.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;

public class AuthApiDocsTest extends RestDocsTestSupport{

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    @DisplayName("사용자가_이메일_인증코드를_발송한다")
    @Test
    void verify_email_code_test(){

        when(emailVerificationService.sendVerificationCode(any(String.class)))
            .thenReturn("123456");

        EmailVerificationRequestDto requestDto = EmailVerificationRequestDto.builder()
            .email("test@example.com")
            .build();

        given(this.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestDto)
            .filter(document("{class_name}/{method_name}",
                requestFields(sendVerifyEmailRequestFields()),
                responseFields(sendVerifyEmailResponseFields())
            ))
            .when()
            .post("/api/v1/auth/email-verification-code")
            .then()
            .log().all()
            .statusCode(200);
    }

    @DisplayName("사용자가_로그인을_진행한다.")
    @Test
    void login_test(){

        Member testMember = AuthTestMemberFactory.createLoginTestMember(passwordEncoder);
        Member savedMember = memberRepository.save(testMember);

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
            .email(savedMember.getEmail())
            .password("pwd1234")
            .build();

        given(this.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(loginRequestDto)
            .filter(document("{class_name}/{method_name}",
                requestFields(loginRequestFields()),
                responseFields(loginResponseFields())
            ))
            .when()
            .post("/api/v1/auth/login")
            .then()
            .log().all()
            .statusCode(200);
    }

    @DisplayName("사용자가_유효한_리프레시_토큰으로_갱신한다.")
    @Test
    void refresh_token_test(){

        Member testMember = AuthTestMemberFactory.createLoginTestMember(passwordEncoder);
        memberRepository.save(testMember);

        String refreshToken = jwtUtil.generateRefreshToken(testMember.getEmail());

        given(this.spec)
            .header(SecurityConstants.HEADER_AUTH, SecurityConstants.TOKEN_PREFIX + refreshToken)
            .filter(document("{class_name}/{method_name}",
                requestHeaders(
                    headerWithName(SecurityConstants.HEADER_AUTH).description("Bearer {refreshToken} 형식의 리프레시 토큰")
                ),
                responseFields(refreshResponseFields())
            ))
            .when()
            .post("/api/v1/auth/refresh")
            .then()
            .log().all()
            .statusCode(200);
    }

    public static List<FieldDescriptor> sendVerifyEmailRequestFields(){
        return List.of(
            fieldWithPath("email").description("이메일 인증에 사용할 이메일 (필수)")
        );
    }

    public static List<FieldDescriptor> sendVerifyEmailResponseFields(){
        return List.of(
            fieldWithPath("message").type(STRING).description("인증 코드 발송 결과 메시지"),
            fieldWithPath("email").type(STRING).description("인증 코드 발송한 이메일 주소"),
            fieldWithPath("verificationCode").type(STRING).description("발송된 인증 코드")
        );
    }

    public static List<FieldDescriptor> loginRequestFields(){

        return List.of(
            fieldWithPath("email").description("로그인할 사용자 이메일 (필수)"),
            fieldWithPath("password").description("로그인할 사용자 비밀번호 (필수)")
        );
    }

    public static List<FieldDescriptor> loginResponseFields(){

        return List.of(
            fieldWithPath("access_token").type(STRING).description("API 요청에 사용할 액세스 토큰"),
            fieldWithPath("refresh_token").type(STRING).description("액세스 토큰 갱신에 사용할 리프레시 토큰"),
            fieldWithPath("member_email").type(STRING).description("로그인한 사용자 이메일"),
            fieldWithPath("member_id").type(NUMBER).description("로그인한 사용자 ID"),
            fieldWithPath("scope").type(STRING).optional().description("토큰 권한 범위")
        );
    }

    public static List<FieldDescriptor> refreshResponseFields() {

        return List.of(
            fieldWithPath("access_token").type(STRING).description("새로 발급된 액세스 토큰"),
            fieldWithPath("refresh_token").type(STRING).description("새로 발급된 리프레시 토큰"),
            fieldWithPath("member_email").type(STRING).description("토큰 소유자 이메일"),
            fieldWithPath("member_id").type(NUMBER).description("토큰 소유자 ID"),
            fieldWithPath("scope").type(STRING).optional().description("토큰 권한 범위")
        );
    }

    public static class AuthTestMemberFactory {

            public static Member createLoginTestMember(PasswordEncoder passwordEncoder) {

        return Member.builder()
            .email("auth_test_" + "_" + System.currentTimeMillis() + "@example.com")
            .password(passwordEncoder.encode("pwd1234"))
            .nickname("authTestUser")
            .birthDate(LocalDateTime.of(1999, 1, 7, 0,0,0))
            .gender(MemberGender.MALE)
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .build();
    }
    }
}
