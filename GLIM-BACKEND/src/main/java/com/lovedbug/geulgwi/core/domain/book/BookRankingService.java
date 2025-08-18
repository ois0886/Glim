package com.lovedbug.geulgwi.core.domain.book;

import lombok.RequiredArgsConstructor;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovedbug.geulgwi.core.common.constant.RedisKey;
import com.lovedbug.geulgwi.core.common.exception.GeulgwiException;
import com.lovedbug.geulgwi.core.domain.book.dto.PopularBookResponse;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.book.mapper.BookMapper;

@RequiredArgsConstructor
@Service
public class BookRankingService {

    private static final double VIEW_INCREMENT_SCORE = 1.0;
    private static final int TOP_RANKING_LIMIT = 10;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public List<PopularBookResponse> getPopularBooks() {
        return Optional.ofNullable(redisTemplate.opsForZSet()
                .reverseRange(RedisKey.POPULAR_BOOKS.getKey(), 0, TOP_RANKING_LIMIT - 1))
            .orElse(Collections.emptySet())
            .stream()
            .map(json -> {
                try {
                    return objectMapper.readValue(json, PopularBookResponse.class);
                } catch (JsonProcessingException e) {
                    throw new GeulgwiException("책 Redis 에서 역직렬화 실패", e);
                }
            })
            .toList();
    }

    public void updateBookRanking(Book book) {
        PopularBookResponse popularBookResponse = BookMapper.toPopularBookResponse(book);

        try {
            String popularBookStr = objectMapper.writeValueAsString(popularBookResponse);
            Double score = calculateScore(popularBookStr, book);

            redisTemplate.opsForZSet().incrementScore(RedisKey.POPULAR_BOOKS.getKey(), popularBookStr, score);

        } catch (JsonProcessingException e) {
            throw new GeulgwiException("글귀 Redis 에서 직렬화 실패", e);
        }
    }

    private Double calculateScore(String popularBookStr, Book book) {
        Double currentScore = redisTemplate.opsForZSet().score(RedisKey.POPULAR_BOOKS.getKey(), popularBookStr);
        Double score = VIEW_INCREMENT_SCORE;

        if (currentScore != null) {
            score *= book.getViews();
        }

        return score;
    }
}
