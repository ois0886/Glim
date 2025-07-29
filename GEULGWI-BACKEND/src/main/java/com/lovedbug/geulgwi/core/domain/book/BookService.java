package com.lovedbug.geulgwi.core.domain.book;

import com.lovedbug.geulgwi.core.domain.book.dto.PopularBookResponse;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.quote.QuoteService;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteWithBookResponse;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.lovedbug.geulgwi.external.book_provider.aladdin.AladdinClient;
import com.lovedbug.geulgwi.external.book_provider.aladdin.constant.AladdinListQueryType;
import com.lovedbug.geulgwi.external.book_provider.aladdin.constant.AladdinSearchQueryType;
import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookListConditionDto;
import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookSearchConditionDto;

@Service
@RequiredArgsConstructor
public class BookService {

    private final AladdinClient bookProviderClient;
    private final BookRepository bookRepository;

    public List<AladdinBookResponse> getBooksByKeyword(AladdinSearchQueryType queryType, String keyword, int page) {
        AladdinBookSearchConditionDto searchCondition = AladdinBookSearchConditionDto.builder()
            .queryType(queryType.name())
            .query(keyword)
            .start(page)
            .build();

        return bookProviderClient.searchBooksByCondition(searchCondition).getItems();
    }

    public List<AladdinBookResponse> getBestSellerBooks(AladdinListQueryType listQueryType, int page) {
        AladdinBookListConditionDto listCondition = AladdinBookListConditionDto.builder()
            .queryType(listQueryType.name())
            .start(page)
            .build();

        return bookProviderClient.getBooks(listCondition).getItems();
    }

    public List<PopularBookResponse> getPopularBooks() {
        List<Book> books = bookRepository.findTop10ByOrderByViewsDesc();

        return books.stream()
            .map(book -> new PopularBookResponse(book.getBookId(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getCoverUrl()))
            .toList();
    }

    @Transactional
    public void increaseViewCount(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("없는 책 id 입니다. 책 id = " + bookId));

        book.increaseViewCount();
    }

    @Transactional
    public void saveBooksFromExternal(List<AladdinBookResponse> aladdinBooks) {
        for (AladdinBookResponse aladdinBook : aladdinBooks) {
            String isbn = aladdinBook.getIsbn();
            if (isbn == null || isbn.isBlank()) {
                continue;
            }

            if (bookRepository.existsByIsbn(isbn)) {
                continue;
            }

            Book book = Book.builder()
                .isbn(aladdinBook.getIsbn())
                .title(aladdinBook.getTitle())
                .author(aladdinBook.getAuthor())
                .category(aladdinBook.getCategoryName())
                .categoryId(aladdinBook.getCategoryId())
                .publisher(aladdinBook.getPublisher())
                .description(aladdinBook.getDescription())
                .isbn(aladdinBook.getIsbn())
                .isbn13(aladdinBook.getIsbn13())
                .publishedDate(
                    LocalDate.parse(aladdinBook.getPublishedDate(), DateTimeFormatter.ISO_LOCAL_DATE))
                .coverUrl(aladdinBook.getCoverUrl())
                .linkUrl(aladdinBook.getLinkUrl())
                .build();

            bookRepository.save(book);
        }
    }
}
