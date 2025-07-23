package com.lovedbug.geulgwi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lovedbug.geulgwi.entity.CurationItemBook;

@Repository
public interface CurationItemBookRepository extends JpaRepository<CurationItemBook,Long> {
}
