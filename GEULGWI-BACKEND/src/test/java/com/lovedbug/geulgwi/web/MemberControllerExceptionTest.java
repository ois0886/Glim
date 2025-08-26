package com.lovedbug.geulgwi.web;

import com.lovedbug.geulgwi.core.common.exception.GlobalExceptionHandler;
import com.lovedbug.geulgwi.core.domain.member.MemberController;
import com.lovedbug.geulgwi.core.domain.member.MemberService;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberErrorCode;
import com.lovedbug.geulgwi.core.domain.member.exception.MemberException;
import com.lovedbug.geulgwi.external.fcm.service.FcmTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class MemberControllerExceptionTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    FcmTokenService fcmTokenService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 조회 : 존재하지 않는 회원 조회 시 커스텀 에러 응답 (404)")
    void get_member_not_found_return_custom_error() throws Exception {

        Long notExistsId = 99L;
        when(memberService.findByMemberId(notExistsId))
            .thenThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        mockMvc.perform(get("/api/v1/members/{memberId}", notExistsId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("MEMBER_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()))
            .andExpect(jsonPath("$.detail").value("memberId = 99"));
    }

    @Test
    @DisplayName("회원 조회: 경로 변수 타입 오류 시 INVALID_PARAMETER 응답 (400)")
    void getMember_typeMismatch_returnsInvalidParameter() throws Exception {
        mockMvc.perform(get("/api/v1/members/{memberId}", "abc")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_PARAMETER"))
            .andExpect(jsonPath("$.message").value("Invalid parameter."))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").exists());
    }
}
