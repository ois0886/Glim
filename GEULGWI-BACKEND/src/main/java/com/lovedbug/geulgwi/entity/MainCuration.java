package com.lovedbug.geulgwi.entity;

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
