package com.lovedbug.geulgwi.entity;

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
