package com.example.myapplication.feature.profile

data class ProfileUiState(
    val profileImageUrl: String? = null,
    val userName: String = "",
    val publishedArticleCount: Int = 0,
    val likedArticleCount: Int = 0,
    val isLoading: Boolean = false,
    val recentArticles: List<RecentArticle> = emptyList()
)

data class RecentArticle(
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