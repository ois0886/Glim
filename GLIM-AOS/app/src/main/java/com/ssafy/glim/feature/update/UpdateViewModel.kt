package com.ssafy.glim.feature.update

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.common.extensions.formatBirthDate
import com.ssafy.glim.core.common.extensions.formatGender
import com.ssafy.glim.core.common.extensions.formatGenderToString
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.domain.usecase.user.GetUserByIdUseCase
import com.ssafy.glim.core.domain.usecase.user.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class UpdateViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel(), ContainerHost<UpdateInfoUiState, UpdateInfoSideEffect> {

    override val container = container<UpdateInfoUiState, UpdateInfoSideEffect>(
        initialState = UpdateInfoUiState(),
    )

    fun getUseCurrentInfo() = intent {
        reduce { state.copy(isLoading = true) }

        runCatching {
            getUserByIdUseCase()
        }.onSuccess { user ->
            reduce {
                state.copy(
                    isLoading = false,
                    userId = user.id,
                    name = user.nickname,
                    email = user.email,
                    gender = user.gender.formatGenderToString(),
                    birthDate = user.birthDate,
                    newName = TextFieldValue(user.nickname)
                )
            }
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ShowError(R.string.error_load_profile_failed))
        }
    }

    fun setUpdateType(type: UpdateType) = intent {
        reduce {
            state.copy(
                updateType = type,
                newNameError = null,
                currentPasswordError = null,
                newPasswordError = null,
                confirmPasswordError = null
            )
        }
    }

    fun onImageSelected(uri: Uri) = intent {
        reduce { state.copy(profileImageUri = uri, isImageSelected = true) }
    }

    fun onNameChanged(name: TextFieldValue) = intent {
        val validationResult = ValidationUtils.validateName(
            name = name.text,
            emptyErrorRes = R.string.error_name_empty,
            invalidErrorRes = R.string.error_name_invalid,
        )

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(newName = name, newNameError = error) }
    }

    fun onProfileImageClicked() = intent {
        postSideEffect(UpdateInfoSideEffect.ShowImagePicker)
    }

    fun onCurrentPasswordChanged(password: TextFieldValue) = intent {
        val validationResult = if (password.text.isNotBlank()) {
            ValidationUtils.validatePassword(
                password = password.text,
                emptyErrorRes = R.string.error_current_password_empty,
                invalidErrorRes = R.string.error_current_password_invalid
            )
        } else {
            ValidationResult.Valid
        }

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(password = password, currentPasswordError = error) }
    }

    fun onNewPasswordChanged(password: TextFieldValue) = intent {
        val passwordValidation = if (password.text.isNotBlank()) {
            ValidationUtils.validatePassword(
                password = password.text,
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
            )
        } else {
            ValidationResult.Valid
        }

        val passwordError = when (passwordValidation) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> passwordValidation.errorMessageRes
        }

        val confirmValidation = if (state.confirmPassword.text.isNotBlank()) {
            ValidationUtils.validatePasswordConfirm(
                password = password.text,
                confirmPassword = state.confirmPassword.text,
                mismatchErrorRes = R.string.error_password_mismatch
            )
        } else {
            ValidationResult.Valid
        }

        val confirmError = when (confirmValidation) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> confirmValidation.errorMessageRes
        }

        reduce {
            state.copy(
                newPassword = password,
                newPasswordError = passwordError,
                confirmPasswordError = confirmError
            )
        }
    }

    fun onConfirmPasswordChanged(confirmPassword: TextFieldValue) = intent {
        val validationResult = ValidationUtils.validatePasswordConfirm(
            password = state.newPassword.text,
            confirmPassword = confirmPassword.text,
            mismatchErrorRes = R.string.error_password_mismatch
        )

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(confirmPassword = confirmPassword, confirmPasswordError = error) }
    }

    fun onSaveClicked() = intent {
        when (state.updateType) {
            UpdateType.PERSONAL -> updatePersonalInfo()
            UpdateType.PASSWORD -> updatePassword()
        }
    }

    private fun updatePersonalInfo() = intent {
        val nameValidation = ValidationUtils.validateName(
            name = state.newName.text,
            emptyErrorRes = R.string.error_name_empty,
            invalidErrorRes = R.string.error_name_invalid,
        )

        val nameError = when (nameValidation) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> nameValidation.errorMessageRes
        }

        reduce { state.copy(newNameError = nameError) }

        if (nameError != null) {
            postSideEffect(UpdateInfoSideEffect.ShowError(nameError))
            return@intent
        }

        reduce { state.copy(isLoading = true) }

        runCatching {
            updateUserUseCase(
                memberId = state.userId,
                password = state.password.text,
                nickname = state.newName.text,
                gender = state.gender.formatGender(),
                birthDate = state.birthDate.formatBirthDate(),
                profileUrl = null
            )
        }.onSuccess { updatedUser ->
            reduce {
                state.copy(
                    isLoading = false,
                    name = updatedUser.nickname,
                    newName = TextFieldValue(updatedUser.nickname)
                )
            }
            postSideEffect(UpdateInfoSideEffect.ProfileUpdated)
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ShowError(R.string.error_update_profile_failed))
        }
    }

    private fun updatePassword() = intent {
        val currentPasswordValidation = ValidationUtils.validatePassword(
            password = state.password.text,
            emptyErrorRes = R.string.error_current_password_empty,
            invalidErrorRes = R.string.error_current_password_invalid
        )

        val newPasswordValidation = ValidationUtils.validatePassword(
            password = state.newPassword.text,
            emptyErrorRes = R.string.error_password_empty,
            invalidErrorRes = R.string.error_password_invalid
        )

        val confirmValidation = ValidationUtils.validatePasswordConfirm(
            password = state.newPassword.text,
            confirmPassword = state.confirmPassword.text,
            mismatchErrorRes = R.string.error_password_mismatch
        )

        val currentPasswordError = when (currentPasswordValidation) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> currentPasswordValidation.errorMessageRes
        }

        val newPasswordError = when (newPasswordValidation) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> newPasswordValidation.errorMessageRes
        }

        val confirmError = when (confirmValidation) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> confirmValidation.errorMessageRes
        }

        reduce {
            state.copy(
                currentPasswordError = currentPasswordError,
                newPasswordError = newPasswordError,
                confirmPasswordError = confirmError
            )
        }

        if (currentPasswordError != null || newPasswordError != null || confirmError != null) {
            val firstError = currentPasswordError ?: newPasswordError ?: confirmError!!
            postSideEffect(UpdateInfoSideEffect.ShowError(firstError))
            return@intent
        }

        reduce { state.copy(isLoading = true) }

        runCatching {
            updateUserUseCase(
                memberId = state.userId,
                password = state.newPassword.text,
                nickname = state.name,
                gender = state.gender.formatGender(),
                birthDate = state.birthDate.formatBirthDate(),
                profileUrl = null
            )
        }.onSuccess { updatedUser ->
            reduce {
                state.copy(
                    isLoading = false,
                    password = TextFieldValue(""),
                    newPassword = TextFieldValue(""),
                    confirmPassword = TextFieldValue(""),
                    currentPasswordError = null,
                    newPasswordError = null,
                    confirmPasswordError = null
                )
            }
            postSideEffect(UpdateInfoSideEffect.PasswordUpdated)
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ShowError(R.string.error_update_password_failed))
        }
    }
}
