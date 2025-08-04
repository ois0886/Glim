package com.ssafy.glim.feature.setting

import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() :
    ViewModel(),
    ContainerHost<SettingUiState, SettingSideEffect> {

    override val container =
        container<SettingUiState, SettingSideEffect>(initialState = SettingUiState())

    fun onAllNotificationsToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                notificationSettings = state.notificationSettings.copy(
                    allNotificationsEnabled = enabled
                )
            )
        }
        postSideEffect(SettingSideEffect.ShowToast(R.string.settings_saved))
    }

    fun onDoNotDisturbModeToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                notificationSettings = state.notificationSettings.copy(
                    doNotDisturbEnabled = enabled
                )
            )
        }
        postSideEffect(SettingSideEffect.ShowToast(R.string.settings_saved))
    }

    fun onDoNotDisturbTimeToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                notificationSettings = state.notificationSettings.copy(
                    doNotDisturbTimeEnabled = enabled
                )
            )
        }
        postSideEffect(SettingSideEffect.ShowToast(R.string.settings_saved))
    }

    fun onWeeklyScheduleToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                notificationSettings = state.notificationSettings.copy(
                    weeklyNotificationsEnabled = enabled
                )
            )
        }
        postSideEffect(SettingSideEffect.ShowToast(R.string.settings_saved))
    }

    fun onLockScreenGlimToggle(enabled: Boolean) = intent {
        reduce {
            state.copy(
                lockScreenSettings = state.lockScreenSettings.copy(
                    glimEnabled = enabled
                )
            )
        }
        postSideEffect(SettingSideEffect.ShowToast(R.string.settings_saved))
    }

    fun onTimeRangeClick() = intent {
        // TODO: 시간 범위 선택 화면으로 이동
        postSideEffect(SettingSideEffect.ShowToast(R.string.not_ready_function))
    }
}
