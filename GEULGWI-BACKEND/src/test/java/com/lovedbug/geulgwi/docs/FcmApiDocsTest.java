package com.lovedbug.geulgwi.docs;

import com.lovedbug.geulgwi.external.fcm.dto.request.FcmTokenRequestDto;
import com.lovedbug.geulgwi.external.fcm.repository.FcmTokenRepository;
import com.lovedbug.geulgwi.external.fcm.service.FcmPushService;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
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
import java.time.LocalDateTime;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@ActiveProfiles("test")
public class FcmApiDocsTest extends RestDocsTestSupport{

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @MockitoBean
    private FcmPushService fcmPushService;

    private Member member;
    private String accessToken;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE fcm_tokens");
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        entityManager.clear();

        member = memberRepository.save(createTestMember());
        accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getMemberId());
    }

    @DisplayName("사용자가 FCM 디바이스 토큰을 등록한다")
    @Test
    void save_fcm_token() {
        FcmTokenRequestDto fcmTokenRequest = FcmTokenRequestDto.builder()
            .deviceToken("test_fcm_token_123456789abcdef")
            .deviceType("ANDROID")
            .deviceId("device_unique_id_001")
            .build();

        given(this.spec)
            .header(JwtUtil.HEADER_AUTH, JwtUtil.TOKEN_PREFIX + accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(fcmTokenRequest)
            .filter(document("{class_name}/{method_name}",
                requestHeaders(
                    headerWithName(JwtUtil.HEADER_AUTH).description("Bearer 액세스 토큰")
                ),
                requestFields(
                    fieldWithPath("deviceToken").description("FCM device 토큰 (필수)"),
                    fieldWithPath("deviceType").description("device 타입 (ANDROID, IOS)"),
                    fieldWithPath("deviceId").description("고유 device 식별자")
                )
            ))
            .when()
            .post("/api/v1/fcm/token")
            .then()
            .log().all()
            .statusCode(200);
    }

    @DisplayName("사용자가 FCM 디바이스 토큰을 비활성화한다")
    @Test
    void inActive_fcm_token() {
        given(this.spec)
            .header(JwtUtil.HEADER_AUTH, JwtUtil.TOKEN_PREFIX + accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .filter(document("{class_name}/{method_name}",
                requestHeaders(
                    headerWithName(JwtUtil.HEADER_AUTH).description("Bearer 액세스 토큰")
                ),
                pathParameters(
                    parameterWithName("deviceId").description("비활성화할 device ID")
                )
            ))
            .when()
            .put("/api/v1/fcm/token/{deviceId}/status", "device_unique_id_001")
            .then()
            .log().all()
            .statusCode(200);

    }

    private Member createTestMember() {
        return Member.builder()
            .email("fcm_test_" + System.currentTimeMillis() + "@example.com")
            .password(passwordEncoder.encode("password123"))
            .nickname("FCM 테스트유저")
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .gender(MemberGender.MALE)
            .birthDate(LocalDateTime.of(1999, 1, 7, 0, 0, 0))
            .build();
    }
}
