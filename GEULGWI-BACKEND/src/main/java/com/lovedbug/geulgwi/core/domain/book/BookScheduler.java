package com.lovedbug.geulgwi.core.domain.book;

import com.lovedbug.geulgwi.external.book_provider.aladdin.constant.AladdinListQueryType;
import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookScheduler {

    private final BookService bookService;
    private int currentPage = 0;

    @Scheduled(cron = "0 0 * * * *")
    public void syncBestSellerBooks() {
        for (int i = 0; i <= 100; i++) {
            List<AladdinBookResponse> books = bookService.getBestSellerBooks(AladdinListQueryType.BESTSELLER, currentPage);
            bookService.saveBooksFromExternal(books);
            currentPage++;
        }
    }
}
