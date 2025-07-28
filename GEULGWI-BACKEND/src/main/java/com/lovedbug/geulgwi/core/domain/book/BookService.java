package com.lovedbug.geulgwi.core.domain.book;

import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Service;
import com.lovedbug.geulgwi.external.book_provider.aladdin.AladdinClient;
import com.lovedbug.geulgwi.external.book_provider.aladdin.AladdinListQueryType;
import com.lovedbug.geulgwi.external.book_provider.aladdin.AladdinSearchQueryType;
import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookListConditionDto;
import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookSearchConditionDto;
import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookDto;

@Service
@RequiredArgsConstructor
public class BookService {

    private final AladdinClient bookProviderClient;
    private final BookRepository bookRepository;

    public List<AladdinBookDto> getBooksByKeyword(AladdinSearchQueryType queryType, String keyword, int page) {
        AladdinBookSearchConditionDto searchCondition = AladdinBookSearchConditionDto.builder()
            .queryType(queryType.name())
            .query(keyword)
            .start(page)
            .build();

        return bookProviderClient.searchBooksByCondition(searchCondition).getItems();
    }

    public List<AladdinBookDto> getBestSellerBooks(AladdinListQueryType listQueryType, int page) {
        AladdinBookListConditionDto listCondition = AladdinBookListConditionDto.builder()
            .queryType(listQueryType.name())
            .start(page)
            .build();

        return bookProviderClient.getBooks(listCondition).getItems();
    }

    @Transactional
    public void increaseViewCount(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("없는 책 id 입니다. 책 id = " + bookId));

        book.increaseViewCount();
    }
}
