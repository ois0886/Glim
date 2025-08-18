package com.ssafy.glim.core.domain.model

data class VerifyEmail(
    val message: String,
    val email: String,
    val verificationCode: String
)
