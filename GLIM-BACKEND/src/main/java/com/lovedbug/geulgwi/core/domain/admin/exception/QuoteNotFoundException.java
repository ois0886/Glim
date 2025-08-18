package com.lovedbug.geulgwi.core.domain.admin.exception;

public class QuoteNotFoundException extends RuntimeException {
    public QuoteNotFoundException(Long id) {
        super("존재하지 않는 QuoteId 입니다. QuoteId: " + id);
    }
}
