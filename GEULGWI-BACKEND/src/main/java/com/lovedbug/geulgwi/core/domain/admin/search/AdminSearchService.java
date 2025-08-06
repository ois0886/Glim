package com.lovedbug.geulgwi.core.domain.admin.search;

import com.lovedbug.geulgwi.core.domain.admin.dto.response.SearchBookResponse;
import com.lovedbug.geulgwi.core.domain.admin.dto.response.SearchQuoteResponse;
import com.lovedbug.geulgwi.core.domain.admin.mapper.SearchBookMapper;
import com.lovedbug.geulgwi.core.domain.admin.mapper.SearchQuoteMapper;
import com.lovedbug.geulgwi.core.domain.book.BookRepository;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSearchService {

    private final BookRepository bookRepository;
    private final QuoteRepository quoteRepository;

    public List<SearchBookResponse> searchBooks(String keyword, Pageable page) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(keyword, page).getContent();
        return SearchBookMapper.toDtoList(books);
    }

    public List<SearchQuoteResponse> searchQuotes(String keyword, Pageable page) {
        List<Quote> quotes = quoteRepository.findByContentContainingIgnoreCase(keyword, page).getContent();
        return SearchQuoteMapper.toDtoList(quotes);
    }
}
