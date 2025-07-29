package com.lovedbug.geulgwi.core.domain.curation.entity;

import com.lovedbug.geulgwi.core.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "main_curation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainCuration extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mainCurationId;
}
