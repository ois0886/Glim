package com.ssafy.glim.feature.profile

data class ProfileUiState(
    val profileImageUrl: String? = null,
    val userName: String = "",
    val publishedGlimCount: Int = 0,
    val likedGlimCount: Int = 0,
    val isLoading: Boolean = false,
    val glimShortCards: List<GlimShortCard> = emptyList()
)

data class GlimShortCard(
    val id: String,
    val title: String,
    val timestamp: String,
    val likeCount: Int,
    val isLiked: Boolean = false
)

sealed class ProfileSideEffect {
    data class ShowToast(val message: String) : ProfileSideEffect()
    data class ShowError(val message: String) : ProfileSideEffect()
}