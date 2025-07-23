package com.lovedbug.geulgwi.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lovedbug.geulgwi.entity.Member;
import com.lovedbug.geulgwi.enums.MemberStatus;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByMemberIdAndStatus(Long memberId, MemberStatus status);
    List<Member> findAllByStatus(MemberStatus status);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
