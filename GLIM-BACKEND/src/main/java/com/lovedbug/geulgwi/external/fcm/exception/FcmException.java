package com.lovedbug.geulgwi.external.fcm.exception;

import org.springframework.http.HttpStatus;

public class FcmException extends RuntimeException{

    private final HttpStatus status;

    public FcmException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus(){
        return status;
    }
}
