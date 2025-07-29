package com.lovedbug.geulgwi.dto.resposne;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticatedUser {

    private Long memberId;
    private String email;
}
