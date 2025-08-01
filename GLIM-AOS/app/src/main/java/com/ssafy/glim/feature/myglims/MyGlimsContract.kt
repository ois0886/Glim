package com.ssafy.glim.feature.myglims

data class MyGlimsUiState(
    val myGlims: List<GlimItem> = emptyList(),
    val currentListType: MyGlimsType = MyGlimsType.LIKED,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
)

sealed interface MyGlimsSideEffect {
    data class ShowToast(val message: String) : MyGlimsSideEffect
}

data class GlimItem(
    val id: Long,
    val content: String,
    val author: String,
    val likeCount: Int,
    val isLiked: Boolean = false,
)

enum class MyGlimsType(val displayName: String) {
    LIKED("좋아요 한 글귀"),
    UPLOADED("업로드 한 글귀"),
}
