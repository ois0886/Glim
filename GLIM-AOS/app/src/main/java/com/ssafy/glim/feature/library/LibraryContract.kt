package com.ssafy.glim.feature.library

import androidx.compose.ui.graphics.Color


data class SearchItem(
    val rankStatus: RankStatus = RankStatus.MAINTAIN,
    val rank: Int = 0,
    val text: String,
    val type: String,
)

enum class RankStatus(val symbol: String, val color: Color) {
    UP("▲", Color.Red),
    DOWN("▼", Color.Blue),
    MAINTAIN("―", Color.Black);
}

val searchItemDummyData = listOf(
    SearchItem(
        rankStatus = RankStatus.UP,
        rank = 1,
        text = "삼성전자",
        type = "주식"
    ),
    SearchItem(
        rankStatus = RankStatus.DOWN,
        rank = 2,
        text = "SK하이닉스",
        type = "주식"
    ),
    SearchItem(
        rankStatus = RankStatus.MAINTAIN,
        rank = 3,
        text = "LG화학",
        type = "주식"
    ),
    SearchItem(
        rankStatus = RankStatus.UP,
        rank = 4,
        text = "카카오",
        type = "IT"
    ),
    SearchItem(
        rankStatus = RankStatus.UP,
        rank = 5,
        text = "네이버",
        type = "IT"
    ),
    SearchItem(
        rankStatus = RankStatus.DOWN,
        rank = 6,
        text = "현대차",
        type = "자동차"
    ),
    SearchItem(
        rankStatus = RankStatus.MAINTAIN,
        rank = 7,
        text = "기아",
        type = "자동차"
    ),
    SearchItem(
        rankStatus = RankStatus.UP,
        rank = 8,
        text = "셀트리온",
        type = "바이오"
    ),
    SearchItem(
        rankStatus = RankStatus.DOWN,
        rank = 9,
        text = "POSCO홀딩스",
        type = "철강"
    ),
    SearchItem(
        rankStatus = RankStatus.UP,
        rank = 10,
        text = "LG에너지솔루션",
        type = "배터리"
    ),
)