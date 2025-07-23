package com.ssafy.glim.core.domain.model

data class Curation(
    val id: Long?,
    val title: String,
    val description: String,
    val type: CurationType,
    val contents: List<CurationContent>
)

