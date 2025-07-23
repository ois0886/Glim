package com.lovedbug.geulgwi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "member_like_book", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"memberId", "bookId"})
})
public class MemberLikeBook extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberLikeBookId;

    private Long memberId;

    private Long bookId;
}

