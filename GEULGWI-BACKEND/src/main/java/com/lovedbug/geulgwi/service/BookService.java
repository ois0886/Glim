package com.lovedbug.geulgwi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Service;
import com.lovedbug.geulgwi.client.AladdinClient;
import com.lovedbug.geulgwi.constant.AladdinListQueryType;
import com.lovedbug.geulgwi.constant.AladdinSearchQueryType;
import com.lovedbug.geulgwi.dto.request.AladdinBookListConditionDto;
import com.lovedbug.geulgwi.dto.request.AladdinBookSearchConditionDto;
import com.lovedbug.geulgwi.dto.resposne.AladdinBookDto;
import com.lovedbug.geulgwi.entity.Book;
import com.lovedbug.geulgwi.repository.BookRepository;

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
