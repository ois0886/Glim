package com.lovedbug.geulgwi.core.domain.admin.dto.response;

public record SearchQuoteResponse (
     Long quoteId,
     String content,
     Integer views,
     Integer page,
     String quoteImage,
     String bookTitle
){

}
