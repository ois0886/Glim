package com.lovedbug.geulgwi.dto.resposne;

import com.lovedbug.geulgwi.enums.MemberGender;
import com.lovedbug.geulgwi.enums.MemberStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberDto {

    @Column(name="member_id")
    private Long memberId;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="nickname")
    private String nickname;

    @Column(name="birth_date")
    private LocalDate birthDate;

    @Column(name="gender")
    private MemberGender gender;

    @Column(name="status")
    private MemberStatus status;
}
