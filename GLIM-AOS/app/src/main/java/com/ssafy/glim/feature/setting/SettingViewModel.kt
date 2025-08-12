package com.ssafy.glim.feature.setting

import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.usecase.setting.GetSettingsUseCase
import com.ssafy.glim.core.domain.usecase.setting.UpdateSettingsUseCase
import com.ssafy.glim.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val navigator: Navigator,
) : ViewModel(), ContainerHost<SettingUiState, SettingSideEffect> {

    override val container = container<SettingUiState, SettingSideEffect>(
        initialState = SettingUiState(
            settings = getSettingsUseCase()
        )
    )

    fun onAllNotificationsToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                settings = state.settings.copy(
                    allNotificationsEnabled = enabled
                )
            )
        }
    }

    fun onDoNotDisturbModeToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                settings = state.settings.copy(
                    doNotDisturbEnabled = enabled
                )
            )
        }
    }

    fun onDoNotDisturbTimeToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                settings = state.settings.copy(
                    doNotDisturbTimeEnabled = enabled
                )
            )
        }
    }

    fun onWeeklyScheduleToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                settings = state.settings.copy(
                    weeklyNotificationsEnabled = enabled
                )
            )
        }
    }

    fun onLockScreenGlimToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                settings = state.settings.copy(
                    isShowGlimEnabled = enabled
                )
            )
        }
    }

    fun onTimeRangeClick() = intent {
        // 구현 필요
    }

    fun onSaveClicked() = intent {
        reduce { state.copy(isLoading = true) }
        runCatching {
            updateSettingsUseCase(
                settings = state.settings
            )
        }.onSuccess {
            reduce {
                state.copy(isLoading = false)
            }
            postSideEffect(SettingSideEffect.ShowError(R.string.settings_saved))
            navigator.navigateBack()
        }.onFailure {
            reduce {
                state.copy(isLoading = false)
            }
            postSideEffect(SettingSideEffect.ShowError(R.string.settings_failed))
        }
    }
}
