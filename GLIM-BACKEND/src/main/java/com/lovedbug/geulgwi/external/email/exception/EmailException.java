package com.lovedbug.geulgwi.external.email.exception;

import com.lovedbug.geulgwi.core.common.exception.GeulgwiException;
import com.lovedbug.geulgwi.external.email.constant.EmailErrorCode;
import lombok.Getter;

@Getter
public class EmailException extends GeulgwiException {

    private final EmailErrorCode errorCode;
    private final String detail;

    public EmailException(EmailErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = null;
    }

    public EmailException(EmailErrorCode errorCode, String detail) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
