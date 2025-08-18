package com.ssafy.glim.core.domain.model.user

data class User(
    val id: Long,
    val email: String,
    val nickname: String,
    val birthDate: String,
    val gender: Gender,
    val status: UserStatus,
    val profileUrl: String?
)

enum class Gender {
    MALE,
    FEMALE
}

enum class UserStatus {
    ACTIVE,
    INACTIVE
}
