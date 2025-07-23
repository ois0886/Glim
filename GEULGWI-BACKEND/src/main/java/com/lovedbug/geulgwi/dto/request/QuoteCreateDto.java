package com.lovedbug.geulgwi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.lovedbug.geulgwi.dto.ImageMetaData;
import com.lovedbug.geulgwi.entity.Book;
import com.lovedbug.geulgwi.entity.Quote;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteCreateDto {

    private Long memberId;
    private String visibility;
    private String content;
    private Integer page;

    private String isbn;
    private BookCreateDto bookCreateData;

    public static Quote toEntity(QuoteCreateDto quoteData, Book book, ImageMetaData imageMetaData) {
        return Quote.builder()
            .memberId(quoteData.getMemberId())
            .visibility(quoteData.getVisibility())
            .content(quoteData.getContent())
            .bookTitle(book.getTitle())
            .page(quoteData.getPage())
            .book(book)
            .imagePath(imageMetaData.imagePath())
            .imageName(imageMetaData.imageName())
            .build();
    }
}
