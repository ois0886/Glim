package com.lovedbug.geulgwi.core.domain.member.dto.request;

import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SignUpRequest {
    private String email;
    private String password;
    private String nickname;
    private LocalDateTime birthDate;
    private MemberGender gender;
}
