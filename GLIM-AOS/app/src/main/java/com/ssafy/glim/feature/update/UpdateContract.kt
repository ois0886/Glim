package com.ssafy.glim.feature.update

import androidx.annotation.StringRes

data class UpdateInfoUiState(
    val name: String = "",
    val email: String = "",
    val profileImageUri: String? = null,
    @StringRes val nameError: Int? = null,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    @StringRes val currentPasswordError: Int? = null,
    @StringRes val newPasswordError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,

    val isLoading: Boolean = false,
    val updateType: UpdateType = UpdateType.PERSONAL
) {
    val isSaveEnabled: Boolean
        get() = when (updateType) {
            UpdateType.PERSONAL -> {
                nameError == null && name.isNotBlank()
            }
            UpdateType.PASSWORD -> {
                currentPasswordError == null &&
                    newPasswordError == null &&
                    confirmPasswordError == null &&
                    currentPassword.isNotBlank() &&
                    newPassword.isNotBlank() &&
                    confirmPassword.isNotBlank()
            }
        }
}

enum class UpdateType {
    PERSONAL,
    PASSWORD
}

sealed interface UpdateInfoSideEffect {
    data class ShowErrorRes(@StringRes val messageRes: Int) : UpdateInfoSideEffect
    data object ShowImagePicker : UpdateInfoSideEffect
    data object ProfileUpdated : UpdateInfoSideEffect
    data object PasswordUpdated : UpdateInfoSideEffect
}

