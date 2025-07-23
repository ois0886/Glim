package com.lovedbug.geulgwi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lovedbug.geulgwi.entity.CurationItem;

@Repository
public interface CurationItemRepository extends JpaRepository<CurationItem, Long> {
}
