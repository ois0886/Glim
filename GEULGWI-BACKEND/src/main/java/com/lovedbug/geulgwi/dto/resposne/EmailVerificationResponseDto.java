package com.lovedbug.geulgwi.dto.resposne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationResponseDto {

    private String message;
    private String email;
    private String verificationCode;
}
