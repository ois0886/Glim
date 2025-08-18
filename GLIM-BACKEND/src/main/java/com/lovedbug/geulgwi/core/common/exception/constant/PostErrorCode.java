package com.lovedbug.geulgwi.core.common.exception.constant;

import com.lovedbug.geulgwi.core.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

    DUPLICATED_POST_REGISTER(HttpStatus.BAD_REQUEST, "Duplicated post register.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
