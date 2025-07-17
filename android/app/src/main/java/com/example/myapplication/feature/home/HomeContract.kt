package com.example.myapplication.feature.home

import com.example.myapplication.feature.home.model.HomeSectionUiModel

data class HomeUiState(
    val isLoading: Boolean = true,
    val sections: List<HomeSectionUiModel> = emptyList()
)

sealed interface HomeSideEffect {
    data class ShowError(val message: String) : HomeSideEffect
}
