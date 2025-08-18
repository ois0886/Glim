package com.lovedbug.geulgwi.core.domain.member.dto.response;

import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberResponse {

    @Column(name="member_id")
    private Long memberId;

    @Column(name="email")
    private String email;

    @Column(name="nickname")
    private String nickname;

    @Column(name="birth_date")
    private LocalDateTime birthDate;

    @Column(name="gender")
    private MemberGender gender;

    @Column(name="status")
    private MemberStatus status;

    @Column(name="profile_url")
    private String profileUrl;
}
