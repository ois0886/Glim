package com.lovedbug.geulgwi.core.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private String memberEmail;
    private Long memberId;
}
