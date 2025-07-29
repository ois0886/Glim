package com.ssafy.glim.feature.update

import android.net.Uri
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
                    newName = user.nickname,
                    selectedGender = user.gender.formatGenderToString(),
                    newBirthDate = user.birthDate
                )
            }
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ShowErrorRes(R.string.error_load_profile_failed))
        }
    }

    fun setUpdateType(type: UpdateType) = intent {
        reduce { state.copy(updateType = type) }
    }

    fun onImageSelected(uri: Uri) = intent {
        reduce { state.copy(profileImageUri = uri.toString()) }
    }

    fun onNameChanged(name: String) = intent {
        val validationResult = ValidationUtils.validateName(
            name = name,
            emptyErrorRes = R.string.error_name_empty,
            invalidErrorRes = R.string.error_name_invalid,
        )

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(newName = name, newNameError = error) }
    }

    fun onGenderChanged(gender: String) = intent {
        reduce { state.copy(selectedGender = gender) }
    }

    fun onBirthDateChanged(birthDate: String) = intent {
        val validationResult = ValidationUtils.validateBirthDate(
            birthDate = birthDate,
            emptyErrorRes = R.string.error_birth_empty,
            invalidErrorRes = R.string.error_birth_format
        )

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(newBirthDate = birthDate, birthDateError = error) }
    }

    fun onProfileImageClicked() = intent {
        postSideEffect(UpdateInfoSideEffect.ShowImagePicker)
    }

    fun onCurrentPasswordChanged(password: String) = intent {
        val validationResult = if (password.isNotBlank()) {
            ValidationUtils.validatePassword(
                password = password,
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

    fun onNewPasswordChanged(password: String) = intent {
        val passwordValidation = if (password.isNotBlank()) {
            ValidationUtils.validatePassword(
                password = password,
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

        val confirmValidation = if (state.confirmPassword.isNotBlank()) {
            ValidationUtils.validatePasswordConfirm(
                password = password,
                confirmPassword = state.confirmPassword,
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

    fun onConfirmPasswordChanged(confirmPassword: String) = intent {
        val validationResult = ValidationUtils.validatePasswordConfirm(
            password = state.newPassword,
            confirmPassword = confirmPassword,
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
            name = state.newName,
            emptyErrorRes = R.string.error_name_empty,
            invalidErrorRes = R.string.error_name_invalid,
        )

        val birthDateValidation = ValidationUtils.validateBirthDate(
            birthDate = state.newBirthDate,
            emptyErrorRes = R.string.error_birth_empty,
            invalidErrorRes = R.string.error_birth_format
        )

        val nameError = if (nameValidation is ValidationResult.Invalid) nameValidation.errorMessageRes else null
        val birthDateError = if (birthDateValidation is ValidationResult.Invalid) birthDateValidation.errorMessageRes else null

        if (nameError != null || birthDateError != null) {
            reduce {
                state.copy(
                    newNameError = nameError,
                    birthDateError = birthDateError
                )
            }
            return@intent
        }

        reduce { state.copy(isLoading = true) }

        runCatching {
            updateUserUseCase(
                memberId = state.userId,
                password = state.password,
                nickname = state.newName,
                gender = state.selectedGender.formatGender(),
                birthDate = state.newBirthDate.formatBirthDate()
            )
        }.onSuccess { updatedUser ->
            reduce {
                state.copy(
                    isLoading = false,
                    name = updatedUser.nickname,
                    gender = updatedUser.gender.formatGenderToString(),
                    birthDate = updatedUser.birthDate
                )
            }
            postSideEffect(UpdateInfoSideEffect.ProfileUpdated)
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ShowErrorRes(R.string.error_update_profile_failed))
        }
    }

    private fun updatePassword() = intent {
        val currentValid = state.password.isNotBlank() && state.currentPasswordError == null
        val newValid = state.newPasswordError == null && state.newPassword.isNotBlank()
        val confirmValid = state.confirmPasswordError == null && state.confirmPassword.isNotBlank()

        if (!currentValid || !newValid || !confirmValid) {
            return@intent
        }

        reduce { state.copy(isLoading = true) }

        runCatching {
            updateUserUseCase(
                memberId = state.userId,
                password = state.newPassword,
                nickname = state.name,
                gender = state.gender.formatGender(),
                birthDate = state.birthDate.formatBirthDate()
            )
        }.onSuccess { updatedUser ->
            reduce {
                state.copy(
                    isLoading = false,
                    password = "",
                    newPassword = "",
                    confirmPassword = "",
                    currentPasswordError = null,
                    newPasswordError = null,
                    confirmPasswordError = null
                )
            }
            postSideEffect(UpdateInfoSideEffect.PasswordUpdated)
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ShowErrorRes(R.string.error_update_password_failed))
        }
    }
}
