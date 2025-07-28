package com.lovedbug.geulgwi.core.domain.curation.repository;

import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurationItemRepository extends JpaRepository<CurationItem, Long> {
}
