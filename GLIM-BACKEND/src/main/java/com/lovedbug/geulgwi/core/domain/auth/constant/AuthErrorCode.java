package com.lovedbug.geulgwi.core.domain.auth.constant;

import com.lovedbug.geulgwi.core.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    EMAIL_NOT_MATCH(HttpStatus.UNAUTHORIZED, "AUTH-001", "이메일이 일치하지 않습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "AUTH-002","비밀번호가 일치하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "유효하지 않거나 만료된 리프레시 토큰입니다."),
    INVALID_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "AUTH-004", "Authorization 헤더가 없거나 형식이 잘못 되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
