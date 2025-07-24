package com.ssafy.glim.feature.glimlist

data class GlimListUiState(
    val glimList: List<GlimItem> = emptyList(),
    val currentListType: GlimListType = GlimListType.LIKED,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

sealed interface GlimListSideEffect {
    data class ShowToast(val message: String) : GlimListSideEffect
}

data class GlimItem(
    val id: Long,
    val content: String,
    val author: String,
    val likeCount: Int,
    val isLiked: Boolean = false
)

enum class GlimListType(val displayName: String) {
    LIKED("좋아요 한 글귀"),
    UPLOADED("업로드 한 글귀")
}
