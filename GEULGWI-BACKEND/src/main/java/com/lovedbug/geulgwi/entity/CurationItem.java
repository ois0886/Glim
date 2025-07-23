package com.lovedbug.geulgwi.entity;

import jakarta.persistence.*;
import lombok.*;
import com.lovedbug.geulgwi.constant.CurationType;

@Entity
@Table(name = "curation_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurationItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curationItemId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurationType curationType;

    private Long mainCurationId;

    private Integer sequence;
}
