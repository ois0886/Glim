package com.lovedbug.geulgwi.core.domain.quote.entity;

import com.lovedbug.geulgwi.core.common.entity.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "member_quote")
public class MemberQuote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberQuoteId;

    private Long memberId;

    private Long quoteId;
}
