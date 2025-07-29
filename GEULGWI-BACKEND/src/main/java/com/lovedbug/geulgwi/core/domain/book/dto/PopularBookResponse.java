package com.lovedbug.geulgwi.core.domain.book.dto;

public record PopularBookResponse(

    Long bookId,
    String bookTitle,
    String author,
    String publisher,

    String bookCoverUrl
) {
}
