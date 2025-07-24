package com.ssafy.glim.core.data.dto.token

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Int,
    val accessTokenExpires: String,
    val refreshTokenExpires: String,
    val userEmail: String,
    val scope: String,
)
