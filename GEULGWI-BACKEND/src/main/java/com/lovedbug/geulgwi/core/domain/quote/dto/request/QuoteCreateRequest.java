package com.lovedbug.geulgwi.core.domain.quote.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lovedbug.geulgwi.core.domain.book.dto.BookCreateRequest;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.lovedbug.geulgwi.external.image.ImageMetaData;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteCreateRequest {

    @JsonIgnore
    private Long memberId;
    private String visibility;
    private String content;
    private Integer page;

    private String isbn;
    private BookCreateRequest bookCreateData;

    public static Quote toEntity(QuoteCreateRequest quoteData, Book book, ImageMetaData imageMetaData) {
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

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

}
