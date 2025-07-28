package com.lovedbug.geulgwi.core.domain.curation.repository;

import java.util.List;
import com.lovedbug.geulgwi.core.domain.curation.entity.MainCuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lovedbug.geulgwi.core.domain.curation.dto.CurationBookDto;
import com.lovedbug.geulgwi.core.domain.curation.dto.CurationQuoteDto;

@Repository
public interface MainCurationRepository extends JpaRepository<MainCuration, Long> {

    @Query(value = """
        SELECT
            ci.curation_item_id AS curationItemId,
            ci.title AS title,
            ci.description AS description,
            b.book_id AS bookId,
            b.title AS bookTitle,
            b.author AS author,
            b.publisher AS publisher,
            b.cover_url AS bookCoverUrl
        FROM
            curation_item ci
        JOIN curation_item_book cb ON ci.curation_item_id = cb.curation_item_id
        JOIN book b ON cb.book_id = b.book_id
        WHERE
            ci.main_curation_id = :curationId
        """, nativeQuery = true)
    List<CurationBookDto> findCurationBooksByCurationId(@Param("curationId") Long curationId);

    @Query(value = """
        SELECT
            ci.curation_item_id AS curationItemId,
            ci.title AS title,
            ci.description AS description,
            q.quote_id AS quoteId,
            b.book_id AS bookId,
            b.title AS bookTitle,
            b.author AS author,
            b.publisher AS publisher,
            q.image_name AS quoteImageName
        FROM
            curation_item ci
        JOIN curation_item_quote cq ON ci.curation_item_id = cq.curation_item_id
        JOIN quote q ON cq.quote_id = q.quote_id
        JOIN book b ON q.book_id = b.book_id
        WHERE
            ci.main_curation_id = :curationId
        """, nativeQuery = true)
    List<CurationQuoteDto> findCurationQuotesByCurationId(@Param("curationId") Long curationId);

}
