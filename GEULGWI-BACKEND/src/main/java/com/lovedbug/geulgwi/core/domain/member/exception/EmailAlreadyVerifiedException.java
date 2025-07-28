package com.lovedbug.geulgwi.core.domain.member.exception;

public class EmailAlreadyVerifiedException extends RuntimeException {

    public EmailAlreadyVerifiedException(String message){
        super(message);
    }
}
