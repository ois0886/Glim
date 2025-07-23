package com.ssafy.glim.core.data.dto.request


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)
