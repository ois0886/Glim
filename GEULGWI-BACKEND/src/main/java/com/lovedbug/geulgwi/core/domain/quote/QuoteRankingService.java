package com.lovedbug.geulgwi.core.domain.quote;

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
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteWithBookResponse;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import com.lovedbug.geulgwi.core.domain.quote.mapper.QuoteMapper;

@RequiredArgsConstructor
@Service
public class QuoteRankingService {

    private static final double VIEW_INCREMENT_SCORE = 1.0;
    private static final int TOP_RANKING_LIMIT = 10;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public List<QuoteWithBookResponse> getPopularQuotesWithBook() {
        return Optional.ofNullable(redisTemplate.opsForZSet()
                .reverseRange(RedisKey.POPULAR_QUOTES.getKey(), 0, TOP_RANKING_LIMIT - 1))
            .orElse(Collections.emptySet())
            .stream()
            .map(json -> {
                try {
                    return objectMapper.readValue(json, QuoteWithBookResponse.class);
                } catch (JsonProcessingException e) {
                    throw new GeulgwiException("글귀 Redis 에서 역직렬화 실패", e);
                }
            })
            .toList();
    }

    public void updateQuoteRanking(Quote quote) {
        QuoteWithBookResponse quoteWithBookResponse = QuoteMapper.toQuoteWithBookResponse(quote);

        try {
            String quoteWithBookStr = objectMapper.writeValueAsString(quoteWithBookResponse);
            Double score = calculateScore(quoteWithBookStr, quote);

            redisTemplate.opsForZSet().incrementScore(RedisKey.POPULAR_QUOTES.getKey(), quoteWithBookStr, score);

        } catch (JsonProcessingException e) {
            throw new GeulgwiException("글귀 Redis 에서 직렬화 실패", e);
        }
    }

    private Double calculateScore(String quoteWithBookStr, Quote quote) {
        Double currentScore = redisTemplate.opsForZSet().score(RedisKey.POPULAR_QUOTES.getKey(), quoteWithBookStr);
        Double score = VIEW_INCREMENT_SCORE;

        if (currentScore != null) {
            score *= quote.getViews();
        }

        return score;
    }
}
