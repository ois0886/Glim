package com.lovedbug.geulgwi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(name = "quote")
public class Quote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quoteId;

    @Column(columnDefinition = "TEXT")
    private String imagePath;

    private String imageName;

    @Column(columnDefinition = "TEXT")
    private String bookTitle;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 10)
    @Builder.Default
    private String visibility = "PUBLIC";

    @Builder.Default
    private Integer views = 0;

    @Builder.Default
    private Integer page = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id",  nullable = false, updatable = false)
    private Book book;

    @Column(nullable = false)
    private Long memberId;

    public void increaseViewCount() {
        this.views += 1;
    }
}

