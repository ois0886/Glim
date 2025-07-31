package com.lovedbug.geulgwi.core.domain.quote.entity;

import com.lovedbug.geulgwi.core.common.entity.BaseTimeEntity;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.like.entity.MemberLikeQuote;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

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

    @OneToMany(mappedBy = "quote", fetch = FetchType.LAZY)
    private List<MemberLikeQuote> likes;

    @Column(nullable = false)
    private Long memberId;

    public void increaseViewCount() {
        this.views += 1;
    }
}

