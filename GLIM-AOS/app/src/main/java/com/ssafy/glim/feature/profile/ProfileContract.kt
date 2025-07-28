package com.ssafy.glim.feature.profile

import androidx.annotation.StringRes

data class ProfileUiState(
    val profileImageUrl: String? = null,
    val userName: String = "",
    val publishedGlimCount: Int = 0,
    val likedGlimCount: Int = 0,
    val isLoading: Boolean = false,
    val glimShortCards: List<GlimShortCard> = emptyList(),
    val withdrawalDialogState: WithdrawalDialogState = WithdrawalDialogState.Hidden,
    val userInputText: String = "",
    val isWithdrawalLoading: Boolean = false,
    val countdownSeconds: Int = 0
)

data class GlimShortCard(
    val id: String,
    val title: String,
    val timestamp: String,
    val likeCount: Int,
    val isLiked: Boolean = false
)

sealed class WithdrawalDialogState {
    object Hidden : WithdrawalDialogState()
    object Warning : WithdrawalDialogState()
    object Confirmation : WithdrawalDialogState()
    object Processing : WithdrawalDialogState()
}

sealed class ProfileSideEffect {
    data class ShowToast(@StringRes val messageRes: Int) : ProfileSideEffect()
    data class ShowError(@StringRes val messageRes: Int) : ProfileSideEffect()
}
