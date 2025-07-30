package com.lovedbug.geulgwi.core.domain.like;

import com.lovedbug.geulgwi.core.domain.like.entity.MemberLikeQuote;
import com.lovedbug.geulgwi.core.domain.like.repository.MemberLikeQuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberLikeQuoteService {

    private final MemberLikeQuoteRepository memberLikeQuoteRepository;

    @Transactional
    public void likeQuote(Long memberId, Long quoteId) {

        if (!memberLikeQuoteRepository.existsByMemberIdAndQuoteId(memberId, quoteId)) {
            MemberLikeQuote likeQuote = MemberLikeQuote.builder()
                .memberId(memberId)
                .quoteId(quoteId)
                .build();
            memberLikeQuoteRepository.save(likeQuote);
        }
    }

    @Transactional
    public void unlikeQuote(Long memberId, Long quoteId) {

        memberLikeQuoteRepository.deleteByMemberIdAndQuoteId(memberId, quoteId);
    }

    @Transactional(readOnly = true)
    public boolean isLikedBy(Long memberId, Long quoteId){

        return memberLikeQuoteRepository.existsByMemberIdAndQuoteId(memberId, quoteId);
    }

    @Transactional(readOnly = true)
    public long countLikes(Long quoteId){

        return memberLikeQuoteRepository.countByQuoteId(quoteId);
    }
}
