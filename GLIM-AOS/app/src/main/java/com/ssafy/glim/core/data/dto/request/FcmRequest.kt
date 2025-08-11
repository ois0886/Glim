package com.ssafy.glim.core.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class FcmRequest(
    val deviceToken: String,
    val deviceType: String,
    val deviceId: String
)
