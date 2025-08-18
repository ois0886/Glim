package com.ssafy.glim.feature.profile

import androidx.annotation.StringRes
import com.ssafy.glim.core.domain.model.QuoteSummary

data class ProfileUiState(
    val profileImageUrl: String? = null,
    val userName: String = "",
    val publishedGlimCount: Int = 0,
    val likedGlimCount: Int = 0,
    val uploadQuotes: List<QuoteSummary> = emptyList(),
    val error: Boolean = false,
    val isRefreshing: Boolean = false,
    val logoutDialogState: LogoutDialogState = LogoutDialogState.Hidden,
    val withdrawalDialogState: WithdrawalDialogState = WithdrawalDialogState.Hidden,
    val editProfileDialogState: EditProfileDialogState = EditProfileDialogState.Hidden,
    val userInputText: String = "",
    val countdownSeconds: Int = 0,
    val isWithdrawalLoading: Boolean = false,
)

enum class EditProfileDialogState {
    Hidden,
    Showing
}

enum class WithdrawalDialogState {
    Hidden,
    Warning,
    Confirmation,
    Processing
}

enum class LogoutDialogState {
    Hidden,
    Confirmation,
    Processing
}

sealed class ProfileSideEffect {
    data class ShowError(@StringRes val messageRes: Int) : ProfileSideEffect()
}
