package com.lovedbug.geulgwi.core.domain.admin.dto.response;

public record SearchBookResponse (

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
