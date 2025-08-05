package com.lovedbug.geulgwi.core.domain.fcm;

import com.lovedbug.geulgwi.core.domain.fcm.entity.FcmTokens;
import com.lovedbug.geulgwi.core.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmTokens, Long> {

    List<FcmTokens> findAllByMemberAndIsActive(Member member, boolean isActive);
}
