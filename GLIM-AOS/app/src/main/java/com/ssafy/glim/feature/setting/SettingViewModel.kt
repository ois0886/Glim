package com.ssafy.glim.feature.setting

import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.LockSettings
import com.ssafy.glim.core.domain.usecase.setting.GetLockSettingsUseCase
import com.ssafy.glim.core.domain.usecase.setting.UpdateLockSettingsUseCase
import com.ssafy.glim.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getLockSettingsUseCase: GetLockSettingsUseCase,
    private val updateLockSettingsUseCase: UpdateLockSettingsUseCase,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<SettingUiState, SettingSideEffect> {

    override val container =
        container<SettingUiState, SettingSideEffect>(initialState = SettingUiState())

    fun loadSettings() = intent {
        getLockSettingsUseCase()
            .catch { throwable ->
                postSideEffect(SettingSideEffect.ShowError(R.string.error_load_settings_failed))
            }
            .collect { settings ->
                reduce {
                    state.copy(
                        lockSettings = LockSettings(
                            isEnabled = settings.isEnabled
                        )
                    )
                }
            }
    }

    fun onAllNotificationsToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                notificationSettings = state.notificationSettings.copy(
                    allNotificationsEnabled = enabled
                )
            )
        }
    }

    fun onDoNotDisturbModeToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                notificationSettings = state.notificationSettings.copy(
                    doNotDisturbEnabled = enabled
                )
            )
        }
    }

    fun onDoNotDisturbTimeToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                notificationSettings = state.notificationSettings.copy(
                    doNotDisturbTimeEnabled = enabled
                )
            )
        }
    }

    fun onWeeklyScheduleToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                notificationSettings = state.notificationSettings.copy(
                    weeklyNotificationsEnabled = enabled
                )
            )
        }
    }

    fun onLockScreenGlimToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                lockSettings = state.lockSettings.copy(
                    isEnabled = enabled
                )
            )
        }
    }

    fun onTimeRangeClick() = intent {
    }

    fun onSaveClicked() = intent {
        reduce { state.copy(isLoading = true) }
        runCatching {
            updateLockSettingsUseCase(
                settings = state.lockSettings
            )
        }.onSuccess {
            reduce {
                state.copy(
                    isLoading = false
                )
            }
            postSideEffect(SettingSideEffect.ShowError(R.string.settings_saved))
            navigator.navigateBack()
        }.onFailure {
            reduce {
                state.copy(
                    isLoading = false
                )
            }
            postSideEffect(SettingSideEffect.ShowError(R.string.settings_failed))
        }
    }
}
