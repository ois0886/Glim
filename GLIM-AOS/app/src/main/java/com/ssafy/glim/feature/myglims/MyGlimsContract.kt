package com.ssafy.glim.feature.myglims

import com.ssafy.glim.core.domain.model.QuoteSummary

data class MyGlimsUiState(
    val myGlims: List<QuoteSummary> = emptyList(),
    val currentListType: MyGlimsType = MyGlimsType.LIKED,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
)

sealed interface MyGlimsSideEffect {
    data class ShowToast(val message: String) : MyGlimsSideEffect
}

enum class MyGlimsType(val displayName: String) {
    LIKED("좋아요 한 글림"),
    UPLOADED("업로드 한 글림"),
}
