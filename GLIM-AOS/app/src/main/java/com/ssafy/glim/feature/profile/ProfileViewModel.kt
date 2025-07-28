package com.ssafy.glim.feature.profile

import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.usecase.auth.LogOutUseCase
import com.ssafy.glim.core.domain.usecase.user.DeleteUserUseCase
import com.ssafy.glim.core.domain.usecase.user.GetUserByIdUseCase
import com.ssafy.glim.core.navigation.MyGlimsRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val navigator: Navigator,
    private val logOutUseCase: LogOutUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel(), ContainerHost<ProfileUiState, ProfileSideEffect> {

    override val container: Container<ProfileUiState, ProfileSideEffect> =
        container(initialState = ProfileUiState())

    fun navigateToGlimLikedList() = intent {
        navigator.navigate(MyGlimsRoute.Liked)
    }

    fun navigateToGlimUploadList() = intent {
        navigator.navigate(MyGlimsRoute.Upload)
    }

    fun navigateToEditProfile() = intent {
        navigator.navigate(Route.UpdateInfo)
    }

    fun navigateToLockSettings() = intent {
        postSideEffect(ProfileSideEffect.ShowToast(R.string.lock_settings_message))
    }

    fun navigateToNotificationSettings() = intent {
        postSideEffect(ProfileSideEffect.ShowToast(R.string.notification_settings_message))
    }

    fun loadProfileData() = intent {
        reduce { state.copy(isLoading = true) }
        runCatching { getUserByIdUseCase() }
            .onSuccess { user ->
                reduce { state.copy(isLoading = false, userName = user.nickname) }
            }
            .onFailure {
                reduce { state.copy(isLoading = false) }
                postSideEffect(ProfileSideEffect.ShowError(R.string.error_load_profile_failed))
            }
    }

    fun onLogOutClick() = intent {
        reduce { state.copy(isLoading = true) }
        runCatching { logOutUseCase() }
            .onSuccess {
                reduce { state.copy(isLoading = false) }
                postSideEffect(ProfileSideEffect.ShowToast(R.string.logout_success))
                navigator.navigate(
                    route = Route.Login,
                    saveState = true,
                    launchSingleTop = true,
                    inclusive = true
                )
            }
            .onFailure {
                reduce { state.copy(isLoading = false) }
                postSideEffect(ProfileSideEffect.ShowError(R.string.logout_failed))
            }
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
        // Countdown without separate job
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
                    postSideEffect(ProfileSideEffect.ShowToast(R.string.withdrawal_success))
                    navigator.navigate(
                        route = Route.Login,
                        saveState = true,
                        launchSingleTop = true,
                        inclusive = true
                    )
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
    }
}
