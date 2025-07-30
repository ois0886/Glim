package com.lovedbug.geulgwi.core.domain.book;

import com.lovedbug.geulgwi.external.book_provider.aladdin.constant.AladdinListQueryType;
import com.lovedbug.geulgwi.external.book_provider.aladdin.constant.AladdinSearchQueryType;
import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookScheduler {

    protected static final Set<String> keywords = new LinkedHashSet<>(
        Set.of("인생", "기록", "하루", "시간", "마음", "사랑", "여행", "철학", "습관")
    );

    private final BookService bookService;

    @Scheduled(cron = "0 0/30 * * * *")
    public void syncBestSellerBooks() {
        if (!keywords.isEmpty()) {

            String keyword = keywords.iterator().next();

            for (int i = 0; i <= 50; i++) {
                log.info("book 마이그레이션 스케줄링 시작, keyword = " + keyword);
                List<AladdinBookResponse> books = bookService.getBooksByKeyword(AladdinSearchQueryType.KEYWORD, keyword, i);
                bookService.saveBooksFromExternal(books);
            }

            keywords.remove(keyword);
        }
    }
}
