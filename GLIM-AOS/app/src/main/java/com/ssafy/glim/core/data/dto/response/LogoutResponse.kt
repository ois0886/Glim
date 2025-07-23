package com.ssafy.glim.core.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class LogoutResponse(
    @SerializedName("message")
    val message: String,
)
