package com.lovedbug.geulgwi.core.domain.quote;

import com.lovedbug.geulgwi.core.domain.quote.dto.request.QuoteCreateRequest;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteResponse;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteWithBookResponse;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.lovedbug.geulgwi.external.image.ImageMetaData;
import com.lovedbug.geulgwi.core.domain.book.dto.BookCreateRequest;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.external.image.handler.ImageHandler;
import com.lovedbug.geulgwi.core.domain.book.BookRepository;


@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final BookRepository bookRepository;
    private final ImageHandler imageHandler;

    public List<QuoteWithBookResponse> getQuotes(Pageable pageable) {
        List<Quote> quotes = quoteRepository.findPublicQuotes(pageable);

        return quotes.stream()
            .map(QuoteService::toDto)
            .toList();
    }

    public List<QuoteResponse> getPublicQuotesByIsbn(String isbn){
        List<Quote> quotes = quoteRepository.findAllByBookIsbnAndVisibility(isbn, "PUBLIC");

        return quotes.stream()
            .map(QuoteResponse::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void increaseViewCount(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException("없는 글귀 id 입니다. 글귀 id = " + quoteId));

        quote.increaseViewCount();
    }

    @Transactional
    public void createQuote(QuoteCreateRequest quoteData, MultipartFile quoteImage) {
        Book book = bookRepository.findBookByIsbn(quoteData.getIsbn())
            .orElseGet(() -> bookRepository.save(BookCreateRequest.toEntity(quoteData.getBookCreateData())));

        ImageMetaData imageMetaData = imageHandler.saveImage(quoteImage);

        quoteRepository.save(QuoteCreateRequest.toEntity(quoteData, book, imageMetaData));
    }

    public List<QuoteWithBookResponse> getPopularQuotesWithBook() {
        List<Quote> quotes = quoteRepository.findPublicQuotes(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "views")));

        return quotes.stream()
            .map(QuoteService::toDto)
            .toList();
    }

    public static QuoteWithBookResponse toDto(Quote quote) {
        return QuoteWithBookResponse.builder()
            .quoteId(quote.getQuoteId())
            .quoteImageName(quote.getImageName())
            .page(quote.getPage())
            .bookId(quote.getBook().getBookId())
            .bookTitle(quote.getBook().getTitle())
            .author(quote.getBook().getAuthor())
            .publisher(quote.getBook().getPublisher())
            .bookCoverUrl(quote.getBook().getCoverUrl())
            .build();
    }

}
