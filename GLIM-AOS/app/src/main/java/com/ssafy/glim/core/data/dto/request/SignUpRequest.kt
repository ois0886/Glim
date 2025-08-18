package com.ssafy.glim.core.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val nickname: String,
    val password: String,
    val gender: String,
    val birthDate: List<Int>,
)
