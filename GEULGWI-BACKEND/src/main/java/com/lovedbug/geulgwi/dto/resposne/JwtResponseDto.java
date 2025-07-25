package com.lovedbug.geulgwi.dto.resposne;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtResponseDto {

    private String accessToken;
    private String memberEmail;
    private Long memberId;
}
