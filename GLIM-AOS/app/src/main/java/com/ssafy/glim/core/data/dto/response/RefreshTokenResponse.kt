package com.ssafy.glim.core.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("access_token_expires")
    val accessTokenExpires: String,
    @SerializedName("refresh_token_expires")
    val refreshTokenExpires: String,
    @SerializedName("user_email")
    val userEmail: String,
    @SerializedName("expired")
    val expired: Boolean,
    @SerializedName("authorizationHeader")
    val authorizationHeader: String,
)
