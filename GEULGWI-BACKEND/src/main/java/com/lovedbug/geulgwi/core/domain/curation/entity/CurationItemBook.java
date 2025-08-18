package com.lovedbug.geulgwi.core.domain.curation.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curation_item_book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurationItemBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curationItemBookId;

    @Column(nullable = false)
    private Long curationItemId;

    @Column(nullable = false)
    private Long bookId;
}
