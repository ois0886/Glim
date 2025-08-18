package com.lovedbug.geulgwi.core.domain.auth.exception;

import com.lovedbug.geulgwi.core.common.exception.GeulgwiException;
import com.lovedbug.geulgwi.core.domain.auth.constant.AuthErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends GeulgwiException {

    private final AuthErrorCode errorCode;
    private final String detail;

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = null;
    }

    public AuthException(AuthErrorCode errorCode, String detail) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
