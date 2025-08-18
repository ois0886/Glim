package com.ssafy.glim.core.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val memberId: Long,
    val email: String,
    val password: String,
    val nickname: String,
    val birthDate: String,
    val gender: String,
    val status: String,
    val profileUrl: String?
)
