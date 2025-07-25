package com.lovedbug.geulgwi.dto.request;

import com.lovedbug.geulgwi.enums.MemberGender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateRequestDto {

    private String password;
    private String nickname;
    private LocalDateTime birthDate;
    private MemberGender gender;
}
