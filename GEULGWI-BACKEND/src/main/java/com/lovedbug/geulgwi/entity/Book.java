package com.lovedbug.geulgwi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "book")
public class Book extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false)
    private String title;

    private String author;

    private String translator;

    private String category;

    private Integer categoryId;

    private String publisher;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String isbn;

    private String isbn13;

    private LocalDate publishedDate;

    private String coverUrl;

    private String linkUrl;

    @Builder.Default
    private Integer views = 0;

    public void increaseViewCount() {
        this.views += 1;
    }
}

