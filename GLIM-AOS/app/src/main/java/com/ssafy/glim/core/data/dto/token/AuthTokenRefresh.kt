package com.ssafy.glim.core.data.dto.token

data class AuthTokenRefresh(
    val accessToken: String,
    val memberEmail: String,
    val memberId: Int
)
