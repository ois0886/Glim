package com.ssafy.glim.feature.update

import androidx.annotation.StringRes

data class UpdateInfoUiState(
    val userId: Long = 0L,
    val name: String = "",
    val email: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val newName: String = "",
    val profileImageUri: String? = null,
    val password: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    @StringRes val newNameError: Int? = null,
    @StringRes val currentPasswordError: Int? = null,
    @StringRes val newPasswordError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,
    val isLoading: Boolean = false,
    val updateType: UpdateType = UpdateType.PERSONAL
) {
    val isSaveEnabled: Boolean
        get() = when (updateType) {
            UpdateType.PERSONAL -> {
                newNameError == null &&
                    newName.isNotBlank() &&
                    newName != name
            }

            UpdateType.PASSWORD -> {
                currentPasswordError == null &&
                    newPasswordError == null &&
                    confirmPasswordError == null &&
                    password.isNotBlank() &&
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
