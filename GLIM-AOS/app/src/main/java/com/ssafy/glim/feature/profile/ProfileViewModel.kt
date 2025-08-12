package com.ssafy.glim.feature.profile

import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.data.authmanager.LogoutReason
import com.ssafy.glim.core.domain.usecase.quote.GetMyLikedQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.GetMyUploadQuoteUseCase
import com.ssafy.glim.core.domain.usecase.user.DeleteUserUseCase
import com.ssafy.glim.core.domain.usecase.user.GetUserByIdUseCase
import com.ssafy.glim.core.navigation.MyGlimsRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.core.navigation.UpdateInfoRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val navigator: Navigator,
    private val authManager: AuthManager,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val getMyUploadQuoteUseCase: GetMyUploadQuoteUseCase,
    private val getMyLikedQuoteUseCase: GetMyLikedQuoteUseCase
) : ViewModel(), ContainerHost<ProfileUiState, ProfileSideEffect> {

    override val container: Container<ProfileUiState, ProfileSideEffect> =
        container(initialState = ProfileUiState())

    fun navigateToGlimsLiked() = intent {
        navigator.navigate(MyGlimsRoute.Liked)
    }

    fun navigateToGlimsUpload() = intent {
        navigator.navigate(MyGlimsRoute.Upload)
    }

    fun navigateToEditProfile() = intent {
        reduce { state.copy(editProfileDialogState = EditProfileDialogState.Showing) }
    }

    fun navigateToPersonalInfo() = intent {
        reduce { state.copy(editProfileDialogState = EditProfileDialogState.Hidden) }
        navigator.navigate(UpdateInfoRoute.Personal)
    }

    fun navigateToPasswordChange() = intent {
        reduce { state.copy(editProfileDialogState = EditProfileDialogState.Hidden) }
        navigator.navigate(UpdateInfoRoute.Password)
    }

    fun onEditProfileDialogCancel() = intent {
        reduce { state.copy(editProfileDialogState = EditProfileDialogState.Hidden) }
    }

    fun navigateToSettings() = intent {
        navigator.navigate(Route.Setting)
    }

    fun loadProfileData() = intent {
        reduce { state.copy(isRefreshing = true, error = false) }

        coroutineScope {
            try {
                val userDeferred = async { runCatching { getUserByIdUseCase() } }
                val uploadQuotesDeferred = async { runCatching { getMyUploadQuoteUseCase() } }
                val likedQuotesDeferred = async { runCatching { getMyLikedQuoteUseCase() } }

                val userResult = userDeferred.await()
                val uploadQuotesResult = uploadQuotesDeferred.await()
                val likedQuotesResult = likedQuotesDeferred.await()

                if (userResult.isSuccess && uploadQuotesResult.isSuccess && likedQuotesResult.isSuccess) {
                    val user = userResult.getOrThrow()
                    val uploadQuotes = uploadQuotesResult.getOrThrow()
                    val likedQuotes = likedQuotesResult.getOrThrow()

                    reduce {
                        state.copy(
                            userName = user.nickname,
                            profileImageUrl = null,
                            publishedGlimCount = uploadQuotes.size,
                            uploadQuotes = uploadQuotes,
                            likedGlimCount = likedQuotes.size,
                            isRefreshing = false,
                            error = false
                        )
                    }
                } else {
                    reduce {
                        state.copy(
                            profileImageUrl = null,
                            userName = "",
                            publishedGlimCount = 0,
                            likedGlimCount = 0,
                            uploadQuotes = emptyList(),
                            isRefreshing = false,
                            error = true
                        )
                    }
                }
            } catch (_: Exception) {
                reduce {
                    state.copy(
                        profileImageUrl = null,
                        userName = "",
                        publishedGlimCount = 0,
                        likedGlimCount = 0,
                        uploadQuotes = emptyList(),
                        isRefreshing = false,
                        error = true
                    )
                }
                postSideEffect(ProfileSideEffect.ShowError(R.string.error_load_profile_failed))
            }
        }
    }

    fun onLogOutClick() = intent {
        reduce { state.copy(logoutDialogState = LogoutDialogState.Confirmation) }
    }

    fun onLogoutConfirm() = intent {
        reduce { state.copy(logoutDialogState = LogoutDialogState.Processing) }
        authManager.logout(LogoutReason.UserLogout)
        postSideEffect(ProfileSideEffect.ShowError(R.string.logout_success))
    }

    fun onLogoutCancel() = intent {
        reduce { state.copy(logoutDialogState = LogoutDialogState.Hidden) }
    }

    fun onWithdrawalClick() = intent {
        reduce {
            state.copy(
                withdrawalDialogState = WithdrawalDialogState.Warning
            )
        }
    }

    fun onWarningConfirm() = intent {
        reduce {
            state.copy(
                withdrawalDialogState = WithdrawalDialogState.Confirmation,
                countdownSeconds = 10
            )
        }
        startCountdown()
    }

    @OptIn(OrbitExperimental::class)
    private fun startCountdown() = intent {
        for (i in 10 downTo 0) {
            delay(1_000)
            reduce { state.copy(countdownSeconds = i) }
        }
    }

    fun onWarningCancel() = intent {
        reduce {
            state.copy(
                withdrawalDialogState = WithdrawalDialogState.Hidden,
                userInputText = "",
                countdownSeconds = 0
            )
        }
    }

    fun onUserInputChanged(input: String) = intent {
        reduce { state.copy(userInputText = input) }
    }

    fun onFinalConfirm() = intent {
        if (state.userInputText == "탈퇴하겠습니다" && state.countdownSeconds == 0) {
            processWithdrawal()
        }
    }

    private fun processWithdrawal() = intent {
        reduce {
            state.copy(
                withdrawalDialogState = WithdrawalDialogState.Processing,
                isWithdrawalLoading = true
            )
        }

        runCatching { deleteUserUseCase() }
            .onSuccess {
                reduce {
                    state.copy(
                        withdrawalDialogState = WithdrawalDialogState.Hidden,
                        isWithdrawalLoading = false,
                        userInputText = "",
                        countdownSeconds = 0
                    )
                }
                postSideEffect(ProfileSideEffect.ShowError(R.string.withdrawal_success))
                navigator.navigateAndClearBackStack(Route.Login)
            }
            .onFailure {
                reduce {
                    state.copy(
                        withdrawalDialogState = WithdrawalDialogState.Hidden,
                        isWithdrawalLoading = false,
                        userInputText = "",
                        countdownSeconds = 0
                    )
                }
                postSideEffect(ProfileSideEffect.ShowError(R.string.withdrawal_failed))
            }
    }

    fun onFinalCancel() = intent {
        reduce {
            state.copy(
                withdrawalDialogState = WithdrawalDialogState.Hidden,
                userInputText = "",
                countdownSeconds = 0
            )
        }
    }
}
