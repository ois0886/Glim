package com.lovedbug.geulgwi.core.domain.quote.entity;

import jakarta.persistence.*;
import lombok.Getter;
import com.lovedbug.geulgwi.core.common.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "member_like_quote", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"memberId", "quoteId"})
})
public class MemberLikeQuote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberLikeQuoteId;

    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quoteId", nullable = false)
    private Quote quote;
}
