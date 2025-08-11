package com.ssafy.glim.core.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class GenerateRequest(
    val prompt: String
)
