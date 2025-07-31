package com.lovedbug.geulgwi.core.domain.like.entity;

import com.lovedbug.geulgwi.core.common.entity.BaseTimeEntity;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_like_quote", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"memberId", "quoteId"})
})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberLikeQuote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberLikeQuoteId;

    @Column(nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quoteId", nullable = false)
    private Quote quote;
}
