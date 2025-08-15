package com.lovedbug.geulgwi.docs;

import com.google.firebase.messaging.FirebaseMessaging;
import com.lovedbug.geulgwi.core.domain.auth.dto.request.EmailVerificationRequest;
import com.lovedbug.geulgwi.core.domain.auth.dto.request.LoginRequest;
import com.lovedbug.geulgwi.core.domain.auth.dto.request.LogoutRequest;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.external.email.EmailVerifier;
import com.lovedbug.geulgwi.core.security.JwtUtil;
import com.lovedbug.geulgwi.external.fcm.entity.FcmTokens;
import com.lovedbug.geulgwi.external.fcm.repository.FcmTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.time.LocalDateTime;

import static com.lovedbug.geulgwi.core.security.JwtUtil.HEADER_AUTH;
import static com.lovedbug.geulgwi.core.security.JwtUtil.TOKEN_PREFIX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;

@ActiveProfiles("test")
public class AuthApiDocsTest extends RestDocsTestSupport{

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private EmailVerifier emailVerifier;

    @MockitoBean
    private FirebaseMessaging firebaseMessaging;

    @DisplayName("사용자가_이메일_인증코드를_발송한다")
    @Test
    void verify_email_code_test(){

        when(emailVerifier.sendVerificationCode(any(String.class)))
            .thenReturn("123456");

        EmailVerificationRequest requestDto = EmailVerificationRequest.builder()
            .email("test@example.com")
            .build();

        given(this.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestDto)
            .filter(document("{class_name}/{method_name}",
                requestFields(
                        fieldWithPath("email").description("이메일 인증에 사용할 이메일 (필수)")
                    ),
                responseFields(
                    fieldWithPath("message").type(STRING).description("인증 코드 발송 결과 메시지"),
                    fieldWithPath("email").type(STRING).description("인증 코드 발송한 이메일 주소"),
                    fieldWithPath("verificationCode").type(STRING).description("발송된 인증 코드")
                )
            ))
            .when()
            .post("/api/v1/auth/email-verification-code")
            .then()
            .log().all()
            .statusCode(200);
    }

    @DisplayName("관리자가_관리자_로그인을_진행한다.")
    @Test
    void admin_login_success_test(){

        Member admin = Member.builder()
            .email("admin_" + System.currentTimeMillis() + "@example.com")
            .password(passwordEncoder.encode("pwd1234"))
            .nickname("adminUser")
            .birthDate(LocalDateTime.of(1990, 1, 1, 0, 0, 0))
            .gender(MemberGender.MALE)
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.ADMIN)
            .build();

        Member savedAdmin = memberRepository.save(admin);

        LoginRequest loginRequest = LoginRequest.builder()
            .email(savedAdmin.getEmail())
            .password("pwd1234")
            .build();

        given(this.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(loginRequest)
            .filter(document("{class_name}/{method_name}",
                requestFields(
                    fieldWithPath("email").description("관리자 로그인할 이메일 (필수)"),
                    fieldWithPath("password").description("관리자 로그인할 비밀번호 (필수)")
                ),
                responseFields(
                    fieldWithPath("accessToken").description("API 요청에 사용할 액세스 토큰"),
                    fieldWithPath("refreshToken").description("API 요청에 사용할 리프레쉬 토큰"),
                    fieldWithPath("memberEmail").description("로그인한 관리자 이메일"),
                    fieldWithPath("memberId").description("로그인한 관리자 ID")
                )
            ))
            .when()
            .post("/api/v1/auth/admin/login")
            .then()
            .log().all()
            .statusCode(200);
    }

    @DisplayName("사용자가_로그인을_진행한다.")
    @Test
    void login_test(){

        Member member = AuthTestMemberFactory.createLoginTestMember(passwordEncoder);
        Member savedMember = memberRepository.save(member);

        LoginRequest loginRequest = LoginRequest.builder()
            .email(savedMember.getEmail())
            .password("pwd1234")
            .build();

        given(this.spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(loginRequest)
            .filter(document("{class_name}/{method_name}",
                requestFields(
                    fieldWithPath("email").description("로그인할 사용자 이메일 (필수)"),
                    fieldWithPath("password").description("로그인할 사용자 비밀번호 (필수)")
                ),
                responseFields(
                    fieldWithPath("accessToken").description("API 요청에 사용할 액세스 토큰"),
                    fieldWithPath("refreshToken").description("API 요청에 사용할 리프레쉬 토큰"),
                    fieldWithPath("memberEmail").description("로그인한 사용자 이메일"),
                    fieldWithPath("memberId").description("로그인한 사용자 ID")
                )
            ))
            .when()
            .post("/api/v1/auth/login")
            .then()
            .log().all()
            .statusCode(200);
    }



    @DisplayName("사용자가_로그아웃을_진행한다.")
    @Test
    void logout_test() {

        Member member = AuthTestMemberFactory.createLoginTestMember(passwordEncoder);
        Member savedMember = memberRepository.save(member);

        String accessToken = jwtUtil.generateAccessToken(savedMember.getEmail(), savedMember.getMemberId());

        String deviceId = "device-123";

        fcmTokenRepository.save(
            FcmTokens.builder()
                .member(savedMember)
                .deviceToken("fcm-token-xyz")
                .deviceType("ANDROID")
                .deviceId(deviceId)
                .isActive(true)
                .build()
        );

        LogoutRequest request = LogoutRequest.builder()
            .deviceId(deviceId)
            .build();

        given(this.spec)
            .header(HEADER_AUTH, TOKEN_PREFIX + accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .filter(document("{class_name}/{method_name}",
                requestHeaders(
                    headerWithName(HEADER_AUTH).description("로그인한 사용자의 액세스 토큰")
                ),
                requestFields(
                    fieldWithPath("deviceId").description("로그아웃 처리할 단말의 ID")
                )
            ))
            .when()
            .post("/api/v1/auth/logout")
            .then()
            .log().all()
            .statusCode(204);
    }

    @DisplayName("사용자가_유효한_토큰으로_갱신한다.")
    @Test
    void refresh_token_test() {

        Member testMember = AuthTestMemberFactory.createLoginTestMember(passwordEncoder);
        memberRepository.save(testMember);

        String refreshToken = jwtUtil.generateRefreshToken(testMember.getEmail(), testMember.getMemberId());

        given(this.spec)
            .header(HEADER_AUTH, TOKEN_PREFIX + refreshToken)
            .filter(document("{class_name}/{method_name}",
                requestHeaders(
                    headerWithName(HEADER_AUTH).description("로그인한 사용자의 리프레시 토큰")
                ),
                responseFields(
                    fieldWithPath("accessToken").description("새로 발급된 액세스 토큰"),
                    fieldWithPath("refreshToken").description("새로 발급된 리프레쉬 토큰"),
                    fieldWithPath("memberEmail").description("토큰 소유자 이메일"),
                    fieldWithPath("memberId").description("토큰 소유자 ID")
                )
            ))
            .when()
            .post("/api/v1/auth/refresh")
            .then()
            .log().all()
            .statusCode(200);
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
