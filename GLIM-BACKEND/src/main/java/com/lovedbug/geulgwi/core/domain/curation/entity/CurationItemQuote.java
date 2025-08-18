package com.lovedbug.geulgwi.core.domain.curation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curation_item_quote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurationItemQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curationItemQuoteId;

    @Column(nullable = false)
    private Long curationItemId;

    @Column(nullable = false)
    private Long quoteId;
}
