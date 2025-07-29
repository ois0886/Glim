package com.lovedbug.geulgwi.core.domain.quote.entity;

import com.lovedbug.geulgwi.core.common.entity.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "member_like_quote", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"memberId", "quoteId"})
})
public class MemberLikeQuote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberLikeQuoteId;

    private Long memberId;

    private Long quoteId;
}
