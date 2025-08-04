package com.lovedbug.geulgwi.core.domain.quote.repository;

import com.lovedbug.geulgwi.core.domain.quote.entity.MemberQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MemberQuoteRepository extends JpaRepository<MemberQuote, Long> {

    List<MemberQuote> findAllByMemberId(Long memberId);
}
