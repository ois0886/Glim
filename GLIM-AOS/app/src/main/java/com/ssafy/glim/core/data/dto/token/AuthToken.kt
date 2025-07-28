package com.ssafy.glim.core.data.dto.token

import kotlinx.serialization.Serializable

@Serializable
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val memberEmail: String,
    val memberId: Int
)
