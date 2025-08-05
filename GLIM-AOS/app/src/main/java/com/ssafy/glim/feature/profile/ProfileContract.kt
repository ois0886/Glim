package com.ssafy.glim.feature.profile

import androidx.annotation.StringRes
import com.ssafy.glim.core.domain.model.UploadQuote

data class ProfileUiState(
    // 기본 프로필 정보
    val profileImageUrl: String? = null,
    val userName: String = "",

    // 업로드 글림 관련 정보
    val publishedGlimCount: Int = 0,
    val likedGlimCount: Int = 0,
    val uploadQuotes: List<UploadQuote> = emptyList(),
    val firstUploadDate: String = "",

    // 로딩 상태 - 세분화
    val isLoading: Boolean = false,
    val isProfileLoading: Boolean = false,
    val isQuotesLoading: Boolean = false,

    // 에러 상태
    val profileError: Boolean = false,
    val quotesError: Boolean = false,

    // 기타 UI 상태
    val logoutDialogState: LogoutDialogState = LogoutDialogState.Hidden,
    val withdrawalDialogState: WithdrawalDialogState = WithdrawalDialogState.Hidden,
    val editProfileDialogState: EditProfileDialogState = EditProfileDialogState.Hidden,
    val userInputText: String = "",
    val countdownSeconds: Int = 0,
    val isWithdrawalLoading: Boolean = false,
) {
    // 컴퓨티드 프로퍼티들
    val hasProfileData: Boolean get() = userName.isNotEmpty() && !profileError
    val hasQuotesData: Boolean get() = !quotesError
    val isProfileDataAvailable: Boolean get() = hasProfileData && !isProfileLoading
    val isQuotesDataAvailable: Boolean get() = hasQuotesData && !isQuotesLoading
    val canShowGrassGrid: Boolean get() = isQuotesDataAvailable && uploadQuotes.isNotEmpty()
}

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
