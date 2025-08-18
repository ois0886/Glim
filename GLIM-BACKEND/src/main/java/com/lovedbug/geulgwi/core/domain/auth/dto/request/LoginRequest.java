package com.lovedbug.geulgwi.core.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginRequest {

    private String email;
    private String password;
}
