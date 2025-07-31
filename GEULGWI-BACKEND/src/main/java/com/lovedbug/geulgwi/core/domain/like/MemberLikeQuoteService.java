package com.lovedbug.geulgwi.core.domain.like;

import com.lovedbug.geulgwi.core.common.exception.GeulgwiException;
import com.lovedbug.geulgwi.core.domain.like.entity.MemberLikeQuote;
import com.lovedbug.geulgwi.core.domain.like.repository.MemberLikeQuoteRepository;
import com.lovedbug.geulgwi.core.domain.quote.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberLikeQuoteService {

    private final MemberLikeQuoteRepository memberLikeQuoteRepository;
    private final QuoteRepository quoteRepository;

    @Transactional
    public void likeQuote(Long memberId, Long quoteId) {

        if (!memberLikeQuoteRepository.existsByMemberIdAndQuote_QuoteId(memberId, quoteId)) {
            MemberLikeQuote likeQuote = MemberLikeQuote.builder()
                .memberId(memberId)
                .quote(quoteRepository.findById(quoteId)
                        .orElseThrow(() -> new GeulgwiException("없는 글귀에 대한 좋아요 입니다, quoteId = " + quoteId)))
                .build();
            memberLikeQuoteRepository.save(likeQuote);
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
}
