package com.lovedbug.geulgwi.core.domain.book.dto;

import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCreateRequest {

    private String title;
    private String author;
    private String translator;
    private String category;
    private Integer categoryId;
    private String publisher;
    private String description;
    private String isbn;
    private String isbn13;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate publishedDate;
    private String coverUrl;
    private String linkUrl;

    public static Book toEntity(BookCreateRequest bookCreateData) {
        return Book.builder()
            .title(bookCreateData.getTitle())
            .author(bookCreateData.getAuthor())
            .translator(bookCreateData.getTranslator())
            .category(bookCreateData.getCategory())
            .categoryId(bookCreateData.getCategoryId())
            .publisher(bookCreateData.getPublisher())
            .description(bookCreateData.getDescription())
            .isbn(bookCreateData.getIsbn())
            .isbn13(bookCreateData.getIsbn13())
            .publishedDate(bookCreateData.getPublishedDate())
            .coverUrl(bookCreateData.getCoverUrl())
            .linkUrl(bookCreateData.getLinkUrl())
            .build();
    }
}
