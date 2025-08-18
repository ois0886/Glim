package com.lovedbug.geulgwi.core.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticatedUser {

    private Long memberId;
    private String email;
}
