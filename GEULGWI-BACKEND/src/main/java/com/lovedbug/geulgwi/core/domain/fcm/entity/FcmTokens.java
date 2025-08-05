package com.lovedbug.geulgwi.core.domain.fcm.entity;

import com.lovedbug.geulgwi.core.common.entity.BaseTimeEntity;
import com.lovedbug.geulgwi.core.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fcm_tokens", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id, device_id"})
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokens extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_id")
    private Long fcmTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "device_token", nullable = false, length = 255)
    private String deviceToken;

    @Column(name = "device_type", nullable = false, length = 20)
    private String deviceType;

    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Column(name = "is_active")
    private Boolean isActive;

    public void updateDeviceToken(String deviceToken) {

        this.deviceToken = deviceToken;
    }
}
