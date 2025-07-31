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
        Set.of(
            "삶", "시간", "존재", "인간", "마음", "생각", "관계", "성장", "변화", "선택",
            "습관", "몰입", "치유", "용기", "자존감", "행복", "성찰", "감정", "고요", "멈춤",
            "사랑", "그리움", "이별", "기억", "계절", "여행", "밤", "바람", "별", "꿈",
            "고백", "편지", "하루", "순간", "희망", "외로움", "눈물", "위로", "고마움", "약속",
            "돈", "부자", "투자", "자산", "경제", "성공", "리더십", "전략", "실행", "목표",
            "마케팅", "창업", "생산성", "기획", "직장", "일잘러", "협업", "조직", "성과",
            "뇌", "우주", "진화", "인공지능", "데이터", "기술", "알고리즘", "생명", "로봇",
            "양자", "심리", "뇌과학", "행동", "통계", "인류", "환경", "기후", "문명", "미래",
            "육아", "공부", "글쓰기", "독서", "그림", "요리", "정리", "다이어트", "여행기", "사진",
            "명상", "하루루틴", "아침습관", "시간관리", "커뮤니케이션", "자녀교육", "자기관리", "번아웃", "MBTI", "일기"
        )
    );

    private final BookService bookService;

    @Scheduled(cron = "0 0/30 * * * *")
    public void syncBestSellerBooks() {
        log.info("현재 키워드 목록 {}", keywords);

        if (!keywords.isEmpty()) {
            String keyword = keywords.iterator().next();
            log.info("book 마이그레이션 스케줄링 시작, keyword = " + keyword);

            for (int i = 0; i <= 50; i++) {
                List<AladdinBookResponse> books = bookService.getBooksByKeyword(AladdinSearchQueryType.KEYWORD, keyword, i);
                bookService.saveBooksFromExternal(books);
            }

            keywords.remove(keyword);
        }
    }
}
