package com.lovedbug.geulgwi.core.domain.book.mapper;

import java.time.format.DateTimeFormatter;
import com.lovedbug.geulgwi.core.domain.book.dto.BookInfoResponse;
import com.lovedbug.geulgwi.core.domain.book.dto.PopularBookResponse;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;

public class BookMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static BookInfoResponse toBookInfoResponse(Book book) {
        return new BookInfoResponse(
            book.getBookId(),
            book.getTitle(),
            book.getAuthor(),
            book.getCategory(),
            book.getCategoryId(),
            book.getPublisher(),
            book.getDescription(),
            book.getIsbn(),
            book.getIsbn13(),
            book.getPublishedDate() != null ? book.getPublishedDate().format(DATE_FORMATTER) : null,
            book.getCoverUrl(),
            book.getLinkUrl(),
            book.getViews()
        );
    }

    public static PopularBookResponse toPopularBookResponse(Book book) {
        return PopularBookResponse.builder()
            .bookId(book.getBookId())
            .bookTitle(book.getTitle())
            .author(book.getAuthor())
            .publisher(book.getPublisher())
            .bookCoverUrl(book.getCoverUrl())
            .build();
    }
}
