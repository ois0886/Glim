package com.ssafy.glim.feature.celebrations

/**
 * Celebrations 화면의 UI 상태
 */
data class CelebrationsUiState(
    val nickname: String = "",
    val isLoading: Boolean = false,
)

/**
 * Celebrations 화면의 Side Effect
 */
sealed class CelebrationsSideEffect {
    /**
     * 홈 화면으로 이동
     */
    data object NavigateToLogin : CelebrationsSideEffect()
}
