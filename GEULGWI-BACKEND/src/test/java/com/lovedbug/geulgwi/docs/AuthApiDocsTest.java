package com.lovedbug.geulgwi.docs;

import com.lovedbug.geulgwi.dto.request.EmailVerificationRequestDto;
import com.lovedbug.geulgwi.dto.request.LoginRequestDto;
import com.lovedbug.geulgwi.dto.request.LogoutRequestDto;
import com.lovedbug.geulgwi.dto.request.SignUpRequestDto;
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

import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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

    @DisplayName("사용자가_이메일_인증을_완료한다.")
    @Test
    void verify_email_test(){

        doNothing().when(emailVerificationService).verifyEmailAndCreateMember(any(String.class));

        EmailVerificationRequestDto requestDto = EmailVerificationRequestDto.builder()
            .token("test-verification-token-12345")
            .build();

        given(this.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestDto)
            .filter(document("{class_name}/{method_name}",
                requestFields(verifyEmailRequestFields()),
                responseFields(verifyEmailResponseFields())
            ))
            .when()
            .post("/api/v1/auth/verify-email")
            .then()
            .log().all()
            .statusCode(200);
    }

    @DisplayName("사용자가_인증_이메일_재전송을_요청한다.")
    @Test
    void resend_verification_email_test(){

        doNothing().when(emailVerificationService).resendVerificationEmail(any(SignUpRequestDto.class));

        SignUpRequestDto requestDto = SignUpRequestDto.builder()
            .email("test@example.com")
            .password("password123")
            .nickname("testNickname")
            .birthDate(LocalDate.of(1990, 1, 7))
            .gender(MemberGender.MALE)
            .build();

        given(this.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestDto)
            .filter(document("{class_name}/{method_name}",
                requestFields(resendVerificationRequestFields()),
                responseFields(resendVerificationResponseFields())
            ))
            .when()
            .post("/api/v1/auth/resend-verification")
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
            .header("Authorization", "Bearer " + refreshToken)
            .filter(document("{class_name}/{method_name}",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer {refreshToken} 형식의 리프레시 토큰")
                ),
                responseFields(refreshResponseFields())
            ))
            .when()
            .post("/api/v1/auth/refresh")
            .then()
            .log().all()
            .statusCode(200);
    }

    @DisplayName("사용자가_로그아웃을_진행한다.")
    @Test
    void logout_test(){

        Member testMember = AuthTestMemberFactory.createLoginTestMember(passwordEncoder);
        memberRepository.save(testMember);

        String refreshToken = jwtUtil.generateRefreshToken(testMember.getEmail());

        LogoutRequestDto logoutRequestDto = LogoutRequestDto.builder()
            .refreshToken(refreshToken)
            .build();

        given(this.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(logoutRequestDto)
            .filter(document("{class_name}/{method_name}",
                requestFields(logoutRequestFields()),
                responseFields(logoutResponseFields())
            ))
            .when()
            .post("/api/v1/auth/logout")
            .then()
            .log().all()
            .statusCode(200);
    }

    public static List<FieldDescriptor> verifyEmailRequestFields(){
        return List.of(
            fieldWithPath("token").description("이메일 인증에 사용할 토큰 (필수)")
        );
    }

    public static List<FieldDescriptor> verifyEmailResponseFields(){
        return List.of(
            fieldWithPath("token").type(STRING).optional().description("인증 토큰 (nullable)"),
            fieldWithPath("verified").type(BOOLEAN).description("인증 완료 여부"),
            fieldWithPath("message").type(STRING).description("인증 처리 결과 메시지")
        );
    }

    public static List<FieldDescriptor> resendVerificationRequestFields(){
        return List.of(
            fieldWithPath("email").description("인증 이메일을 재전송할 이메일 주소 (필수)"),
            fieldWithPath("password").description("사용자 비밀번호 (필수)"),
            fieldWithPath("nickname").description("사용자 닉네임 (필수)"),
            fieldWithPath("birthDate").description("사용자 생년월일 (YYYY-MM-DD 형식, 필수)"),
            fieldWithPath("gender").description("사용자 성별 (MALE, FEMALE 중 하나, 필수)")
        );
    }

    public static List<FieldDescriptor> resendVerificationResponseFields(){
        return List.of(
            fieldWithPath("message").type(STRING).description("인증 이메일 재전송 결과 메시지")
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
            fieldWithPath("token_type").type(STRING).description("토큰 타입 (Bearer)"),
            fieldWithPath("expires_in").type(NUMBER).description("액세스 토큰 만료까지 남은 시간(초)"),
            fieldWithPath("access_token_expires").type(STRING).description("액세스 토큰 만료 시각 (ISO 8601)"),
            fieldWithPath("refresh_token_expires").type(STRING).description("리프레시 토큰 만료 시각 (ISO 8601)"),
            fieldWithPath("user_email").type(STRING).description("로그인한 사용자 이메일"),
            fieldWithPath("scope").type(STRING).optional().description("토큰 권한 범위")
        );
    }

    public static List<FieldDescriptor> refreshResponseFields() {

        return List.of(
            fieldWithPath("access_token").type(STRING).description("새로 발급된 액세스 토큰"),
            fieldWithPath("refresh_token").type(STRING).description("새로 발급된 리프레시 토큰"),
            fieldWithPath("token_type").type(STRING).description("토큰 타입 (Bearer)"),
            fieldWithPath("expires_in").type(NUMBER).description("새 액세스 토큰 만료까지 남은 시간(초)"),
            fieldWithPath("access_token_expires").type(STRING).description("새 액세스 토큰 만료 시각 (ISO 8601)"),
            fieldWithPath("refresh_token_expires").type(STRING).description("새 리프레시 토큰 만료 시각 (ISO 8601)"),
            fieldWithPath("user_email").type(STRING).description("토큰 소유자 이메일"),
            fieldWithPath("scope").type(STRING).optional().description("토큰 권한 범위")
        );
    }

    public static List<FieldDescriptor> logoutRequestFields(){

        return List.of(
            fieldWithPath("refreshToken").description("로그아웃 사용자의 리프레시 토큰").optional()
        );
    }

    public static List<FieldDescriptor> logoutResponseFields() {

        return List.of(
            fieldWithPath("message").description("로그아웃 처리 결과 메시지")
        );
    }

    public static class AuthTestMemberFactory {

        private static long counter = 0L;

            public static Member createLoginTestMember(PasswordEncoder passwordEncoder) {
        counter++;
        return Member.builder()
            .email("auth_test_" + counter + "_" + System.currentTimeMillis() + "@example.com")
            .password(passwordEncoder.encode("pwd1234"))
            .nickname("authTestUser" + counter)
            .birthDate(LocalDate.of(1990, 1, 1))
            .gender(MemberGender.MALE)
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .emailVerified(true)  // 이메일 인증 완료 상태로 설정
            .build();
    }
    }
}
