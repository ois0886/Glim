package com.lovedbug.geulgwi.external.fcm.repository;

import com.lovedbug.geulgwi.external.fcm.entity.FcmTokens;
import com.lovedbug.geulgwi.core.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmTokens, Long> {

    Optional<FcmTokens> findByMemberAndDeviceId(Member member, String deviceId);

    List<FcmTokens> findAllByMemberAndIsActive(Member member, boolean isActive);
}
