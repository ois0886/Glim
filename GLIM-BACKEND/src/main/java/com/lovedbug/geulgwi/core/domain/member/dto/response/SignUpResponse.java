package com.lovedbug.geulgwi.core.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {
    private String email;
    private String nickname;
    private String message;
}
