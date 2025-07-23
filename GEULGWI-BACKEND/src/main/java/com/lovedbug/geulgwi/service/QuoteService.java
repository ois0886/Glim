package com.lovedbug.geulgwi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.lovedbug.geulgwi.dto.ImageMetaData;
import com.lovedbug.geulgwi.dto.request.BookCreateDto;
import com.lovedbug.geulgwi.dto.request.QuoteCreateDto;
import com.lovedbug.geulgwi.dto.resposne.QuoteWithBookDto;
import com.lovedbug.geulgwi.entity.Book;
import com.lovedbug.geulgwi.entity.Quote;
import com.lovedbug.geulgwi.external.image.ImageHandler;
import com.lovedbug.geulgwi.repository.BookRepository;
import com.lovedbug.geulgwi.repository.QuoteRepository;


@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final BookRepository bookRepository;
    private final ImageHandler imageHandler;

    public List<QuoteWithBookDto> getQuotes(Pageable pageable) {
        List<Quote> quotes = quoteRepository.findPublicQuotes(pageable);

        return quotes.stream()
            .map(QuoteService::toDto)
            .toList();
    }

    @Transactional
    public void increaseViewCount(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException("없는 글귀 id 입니다. 글귀 id = " + quoteId));

        quote.increaseViewCount();
    }

    @Transactional
    public void createQuote(QuoteCreateDto quoteData, MultipartFile quoteImage) {
        Book book = bookRepository.findBookByIsbn(quoteData.getIsbn())
            .orElseGet(() -> bookRepository.save(BookCreateDto.toEntity(quoteData.getBookCreateData())));

        ImageMetaData imageMetaData = imageHandler.saveImage(quoteImage);

        quoteRepository.save(QuoteCreateDto.toEntity(quoteData, book, imageMetaData));
    }

    public List<QuoteWithBookDto> getPopularQuotesWithBook() {
        List<Quote> quotes = quoteRepository.findPublicQuotes(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "views")));

        return quotes.stream()
            .map(QuoteService::toDto)
            .toList();
    }

    public static QuoteWithBookDto toDto(Quote quote) {
        return QuoteWithBookDto.builder()
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
