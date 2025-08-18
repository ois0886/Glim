package com.ssafy.glim.core.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val email: String,
    val nickname: String,
    val message: String
)
