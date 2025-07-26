package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.VerifyEmailResponse
import com.ssafy.glim.core.domain.model.VerifyEmail

fun VerifyEmailResponse.toDomain() = VerifyEmail(
    message = this.message,
    verificationCode = this.verificationCode,
    email = this.email
)
