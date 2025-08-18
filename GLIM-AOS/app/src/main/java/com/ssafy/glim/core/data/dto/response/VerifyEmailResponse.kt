package com.ssafy.glim.core.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailResponse(
    val message: String,
    val email: String,
    val verificationCode: String
)
