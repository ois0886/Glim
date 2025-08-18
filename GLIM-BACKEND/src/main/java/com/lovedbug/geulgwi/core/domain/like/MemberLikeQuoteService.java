package com.lovedbug.geulgwi.core.domain.like;

import com.lovedbug.geulgwi.core.common.exception.GeulgwiException;
import com.lovedbug.geulgwi.external.fcm.service.FcmPushService;
import com.lovedbug.geulgwi.core.domain.like.entity.MemberLikeQuote;
import com.lovedbug.geulgwi.core.domain.like.repository.MemberLikeQuoteRepository;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteResponse;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberLikeQuoteService {

    private final MemberLikeQuoteRepository memberLikeQuoteRepository;
    private final QuoteRepository quoteRepository;
    private final MemberRepository memberRepository;
    private final FcmPushService fcmPushService;

    @Transactional
    public void likeQuote(Long memberId, Long quoteId) {

        if (!memberLikeQuoteRepository.existsByMemberIdAndQuote_QuoteId(memberId, quoteId)) {
            Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new GeulgwiException("없는 글귀에 대한 좋아요 입니다, quoteId = " + quoteId));

            MemberLikeQuote likeQuote = MemberLikeQuote.builder()
                .memberId(memberId)
                .quote(quote)
                .build();

            memberLikeQuoteRepository.save(likeQuote);

            if (!quote.getMemberId().equals(memberId)) {
                Member member = memberRepository.findById(quote.getMemberId())
                    .orElseThrow(() -> new GeulgwiException("글귀 작성자를 찾을 수 없습니다.  memberId = " + quote.getMemberId()));

                try {
                    fcmPushService.sendLikeNotification(member, quote);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Transactional
    public void unlikeQuote(Long memberId, Long quoteId) {

        memberLikeQuoteRepository.deleteByMemberIdAndQuote_QuoteId(memberId, quoteId);
    }

    @Transactional(readOnly = true)
    public boolean isLikedBy(Long memberId, Long quoteId){

        return memberLikeQuoteRepository.existsByMemberIdAndQuote_QuoteId(memberId, quoteId);
    }

    @Transactional(readOnly = true)
    public long countLikes(Long quoteId){

        return memberLikeQuoteRepository.countByQuote_QuoteId(quoteId);
    }

    @Transactional(readOnly = true)
    public List<QuoteResponse> getLikedQuotesByMember(Long memberId) {

        List<MemberLikeQuote> likeQuotes = memberLikeQuoteRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);

        List<Long> quoteIds = likeQuotes.stream()
            .map(likeQuote -> likeQuote.getQuote().getQuoteId())
            .toList();

        List<Object[]> likeCountList = memberLikeQuoteRepository.countByQuoteIds(quoteIds);

        Map<Long, Long> likeCountMap = likeCountList.stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Long) row[1]
            ));

        return likeQuotes.stream()
            .map(likeQuote -> {
                Quote quote = likeQuote.getQuote();
                long likeCount = likeCountMap.getOrDefault(quote.getQuoteId(), 0L);

                return QuoteResponse.toResponseDto(quote, true, likeCount, null);
            })
            .toList();
    }
}
