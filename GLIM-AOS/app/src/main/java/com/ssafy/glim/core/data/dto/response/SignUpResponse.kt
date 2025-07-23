package com.ssafy.glim.core.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    @SerializedName("memberId")
    val memberId: Long,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("birthDate")
    val birthDate: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("status")
    val status: String
)