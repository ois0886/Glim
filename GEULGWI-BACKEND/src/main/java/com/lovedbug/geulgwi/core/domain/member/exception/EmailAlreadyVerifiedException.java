package com.lovedbug.geulgwi.core.domain.member.exception;

import com.lovedbug.geulgwi.core.common.exception.GeulgwiException;

public class EmailAlreadyVerifiedException extends GeulgwiException {

    public EmailAlreadyVerifiedException(String message){
        super(message);
    }
}
