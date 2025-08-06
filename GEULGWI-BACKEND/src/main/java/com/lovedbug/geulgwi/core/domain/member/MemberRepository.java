package com.lovedbug.geulgwi.core.domain.member;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberIdAndStatus(Long memberId, MemberStatus status);

    List<Member> findAllByStatus(MemberStatus status);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    @Query("""
        SELECT m
          FROM Member m
         WHERE LOWER(m.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(m.email)    LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Page<Member> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
