package com.lovedbug.geulgwi.core.domain.book.dto;

public record BookInfoResponse (

    Long bookId,
    String title,
    String author,
    String categoryName,
    Integer categoryId,
    String publisher,
    String description,
    String isbn,
    String isbn13,
    String publishedDate,
    String coverUrl,
    String linkUrl,
    Integer views
){
}
