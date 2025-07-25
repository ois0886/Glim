package com.ssafy.glim.core.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SearchHistoryResponse(
    val searchHistory: List<String>
)
