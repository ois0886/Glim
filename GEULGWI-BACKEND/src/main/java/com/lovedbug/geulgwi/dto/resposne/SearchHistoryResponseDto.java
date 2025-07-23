package com.lovedbug.geulgwi.dto.resposne;

import com.lovedbug.geulgwi.entity.Book;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchHistoryResponseDto {

    private Long bookId;
    private String title;
    private String author;

    public static SearchHistoryResponseDto toSearchHistoryDto(Book book){
        return SearchHistoryResponseDto.builder()
            .bookId(book.getBookId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .build();
    }
}
