package com.lovedbug.geulgwi.core.domain.book.dto;

import lombok.Builder;

@Builder
public record PopularBookResponse(

    Long bookId,
    String bookTitle,
    String author,
    String publisher,

    String bookCoverUrl
) {
}
