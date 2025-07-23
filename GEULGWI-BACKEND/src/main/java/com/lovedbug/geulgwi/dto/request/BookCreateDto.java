package com.lovedbug.geulgwi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovedbug.geulgwi.entity.Book;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCreateDto {

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

    public static Book toEntity(BookCreateDto bookCreateData) {
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
