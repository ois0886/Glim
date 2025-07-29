package com.lovedbug.geulgwi.core.domain.curation.entity;

import com.lovedbug.geulgwi.core.common.entity.BaseTimeEntity;
import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;
import jakarta.persistence.*;
import lombok.*;

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
