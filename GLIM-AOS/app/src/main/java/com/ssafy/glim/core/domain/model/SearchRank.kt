package com.ssafy.glim.core.domain.model

import androidx.compose.ui.graphics.Color

data class SearchItem(
    val rankStatus: RankStatus = RankStatus.MAINTAIN,
    val rank: Int = 0,
    val text: String,
    val type: String = "",
)

enum class RankStatus(val symbol: String, val color: Color) {
    UP("▲", Color.Red),
    DOWN("▼", Color.Blue),
    MAINTAIN("―", Color.Black);
}
