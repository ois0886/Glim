package com.lovedbug.geulgwi.dto.resposne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {
    private String email;
    private String nickname;
    private String message;
    private boolean emailVerificationSent;
}
