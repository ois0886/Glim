package com.ssafy.glim.feature.update

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.ui.text.input.TextFieldValue

data class UpdateInfoUiState(
    val name: String = "",
    val email: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val newName: TextFieldValue = TextFieldValue(""),
    val profileImageUri: String? = null,
    val password: TextFieldValue = TextFieldValue(""),
    val newPassword: TextFieldValue = TextFieldValue(""),
    val confirmPassword: TextFieldValue = TextFieldValue(""),
    @StringRes val newNameError: Int? = null,
    @StringRes val currentPasswordError: Int? = null,
    @StringRes val newPasswordError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,
    val isLoading: Boolean = false,
    val updateType: UpdateType = UpdateType.PERSONAL,
    val isImageSelected: Boolean = false,
) {
    val isSaveEnabled: Boolean
        get() = when (updateType) {
            UpdateType.PERSONAL -> {
                newNameError == null &&
                    newName.text.isNotBlank() &&
                    newName.text != name || isImageSelected
            }

            UpdateType.PASSWORD -> {
                currentPasswordError == null &&
                    newPasswordError == null &&
                    confirmPasswordError == null &&
                    password.text.isNotBlank() &&
                    newPassword.text.isNotBlank() &&
                    confirmPassword.text.isNotBlank()
            }
        }
}

enum class UpdateType {
    PERSONAL,
    PASSWORD
}

sealed interface UpdateInfoSideEffect {
    data class ShowError(@StringRes val messageRes: Int) : UpdateInfoSideEffect
    data object ShowImagePicker : UpdateInfoSideEffect
    data object ProfileUpdated : UpdateInfoSideEffect
    data object PasswordUpdated : UpdateInfoSideEffect
}
