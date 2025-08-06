package com.ssafy.glim.feature.home

import com.ssafy.glim.feature.home.model.HomeSectionUiModel

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val sections: List<HomeSectionUiModel> = emptyList(),
)

sealed interface HomeSideEffect {
    data class ShowError(val message: String) : HomeSideEffect
}
