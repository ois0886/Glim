package com.lovedbug.geulgwi.core.domain.admin.mapper;

import com.lovedbug.geulgwi.core.domain.admin.dto.response.SearchBookResponse;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import java.util.List;
import java.util.stream.Collectors;

public final class SearchBookMapper {

    public static SearchBookResponse toSearchBookResponse(Book book) {

        return new SearchBookResponse(
            book.getBookId(),
            book.getTitle(),
            book.getAuthor(),
            null,
            null,
            book.getPublisher(),
            book.getDescription(),
            book.getIsbn(),
            book.getIsbn13(),
            book.getPublishedDate().toString(),
            book.getCoverUrl(),
            book.getLinkUrl(),
            book.getViews()
        );
    }

    public static List<SearchBookResponse> toDtoList(List<Book> books) {
        return books.stream()
            .map(SearchBookMapper::toSearchBookResponse)
            .collect(Collectors.toList());
    }
}
