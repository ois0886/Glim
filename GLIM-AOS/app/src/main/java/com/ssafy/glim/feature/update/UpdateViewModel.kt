package com.ssafy.glim.feature.update

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class UpdateViewModel @Inject constructor(
    private val navigator: Navigator,
    // private val updateProfileUseCase: UpdateProfileUseCase,
    // private val updatePasswordUseCase: UpdatePasswordUseCase,
) : ViewModel(), ContainerHost<UpdateInfoUiState, UpdateInfoSideEffect> {

    override val container = container<UpdateInfoUiState, UpdateInfoSideEffect>(
        initialState = UpdateInfoUiState(),
    )

    fun setUpdateType(type: UpdateType) = intent {
        reduce { state.copy(updateType = type) }
    }

    // 개인정보 관련 메서드들
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

        reduce { state.copy(name = name, nameError = error) }
    }

    fun onProfileImageClicked() = intent {
        postSideEffect(UpdateInfoSideEffect.ShowImagePicker)
    }

    // 비밀번호 관련 메서드들
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

        reduce { state.copy(currentPassword = password, currentPasswordError = error) }
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

        // 비밀번호 확인 재검증
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
            name = state.name,
            emptyErrorRes = R.string.error_name_empty,
            invalidErrorRes = R.string.error_name_invalid,
        )

        if (nameValidation is ValidationResult.Invalid) {
            reduce { state.copy(nameError = nameValidation.errorMessageRes) }
            return@intent
        }

        reduce { state.copy(isLoading = true) }

        // TODO: 실제 API 호출
        runCatching {
            delay(1000) // 임시 지연
            // updateProfileUseCase(state.name, state.profileImageUri)
        }.onSuccess {
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ProfileUpdated)
        }.onFailure {
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ShowErrorRes(R.string.error_update_profile_failed))
        }
    }

    private fun updatePassword() = intent {
        // 모든 비밀번호 필드 검증
        val currentValid = state.currentPassword.isNotBlank()
        val newValid = state.newPasswordError == null && state.newPassword.isNotBlank()
        val confirmValid = state.confirmPasswordError == null && state.confirmPassword.isNotBlank()

        if (!currentValid || !newValid || !confirmValid) {
            return@intent
        }

        reduce { state.copy(isLoading = true) }

        // TODO: 실제 API 호출
        runCatching {
            delay(1000) // 임시 지연
            // updatePasswordUseCase(state.currentPassword, state.newPassword)
        }.onSuccess {
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.PasswordUpdated)
        }.onFailure {
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ShowErrorRes(R.string.error_update_password_failed))
        }
    }
}
