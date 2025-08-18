package com.ssafy.glim.core.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val password: String,
    val nickname: String,
    val gender: String,
    val birthDate: String,
)
