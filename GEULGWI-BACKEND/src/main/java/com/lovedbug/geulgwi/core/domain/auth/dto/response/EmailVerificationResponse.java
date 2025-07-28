package com.lovedbug.geulgwi.core.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationResponse {

    private String message;
    private String email;
    private String verificationCode;
}
