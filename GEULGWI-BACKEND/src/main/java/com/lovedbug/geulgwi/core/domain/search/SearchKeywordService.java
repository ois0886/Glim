package com.lovedbug.geulgwi.core.domain.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class SearchKeywordService {

    private static final String POPULAR_KEYWORDS_KEY = "popular:search:keywords";
    private final StringRedisTemplate stringRedisTemplate;

    public List<String> getPopularSearchKeywords() {

        Set<String> topKeywords = Optional.ofNullable(stringRedisTemplate.boundZSetOps(POPULAR_KEYWORDS_KEY)
            .reverseRange(0, 9)).orElse(Collections.emptySet());

        return new ArrayList<>(topKeywords);
    }

    public void increaseKeywordScore(String keyword) {
        stringRedisTemplate.boundZSetOps(POPULAR_KEYWORDS_KEY).incrementScore(keyword, 1);
    }
}
