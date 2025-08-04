package com.ssafy.glim.core.data.authmanager

// 로그아웃 이유를 나타내는 enum
enum class LogoutReason {
    // 최대 재시도 횟수 초과
    MaxRetryExceeded,

    // 토큰 정보 없음
    TokenNotFound,

    // Refresh token 만료
    RefreshTokenExpired,

    // 알 수 없는 오류
    UnknownError,

    // 사용자 수동 로그아웃
    UserLogout,

    // 사용자 탈퇴
    UserWithDrawl
}
