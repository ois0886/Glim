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
    private String token;
    private boolean verified;
    private String message;
}
