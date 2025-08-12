package com.lovedbug.geulgwi.core.domain.member.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode {

    NICKNAME_DUPLICAT0E(HttpStatus.CONFLICT, "MEMBER-001", "이미 사용 중인 닉네임입니다."),
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "MEMBER-002", "이미 가입된 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-003", "회원이 존재하지 않습니다."),
    MEMBER_INACTIVE(HttpStatus.FORBIDDEN, "MEMBER-004", "비활성화 된 회원입니다."),
    PROFILE_IMAGE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-005", "프로필 이미지 저장에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
