package com.ssafy.glim.feature.update

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.common.extensions.formatBirthDate
import com.ssafy.glim.core.common.extensions.formatBirthDateToISO
import com.ssafy.glim.core.common.extensions.formatGender
import com.ssafy.glim.core.common.extensions.formatGenderToString
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.domain.usecase.user.GetUserByIdUseCase
import com.ssafy.glim.core.domain.usecase.user.UpdateUserUseCase
import com.ssafy.glim.core.util.DefaultImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.net.URL
import javax.inject.Inject

@HiltViewModel
internal class UpdateViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel(), ContainerHost<UpdateInfoUiState, UpdateInfoSideEffect> {

    override val container = container<UpdateInfoUiState, UpdateInfoSideEffect>(
        initialState = UpdateInfoUiState(),
    )

    private fun formatBirthDateToISO(birthDate: String): String {
        return try {
            // "1999-01-01" 형식을 "1999-01-07T00:00:00" 형식으로 변환
            "${birthDate}T00:00:00"
        } catch (e: Exception) {
            birthDate // 변환 실패 시 원본 반환
        }
    }

    fun getUseCurrentInfo() = intent {
        reduce { state.copy(isLoading = true) }

        runCatching {
            getUserByIdUseCase()
        }.onSuccess { user ->
            reduce {
                state.copy(
                    isLoading = false,
                    name = user.nickname,
                    email = user.email,
                    gender = user.gender.formatGenderToString(),
                    birthDate = user.birthDate,
                    profileImageUri = user.profileUrl,
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
        reduce { state.copy(profileImageUri = uri.toString(), isImageSelected = true) }
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

    fun onSaveClicked(context: Context) = intent {
        when (state.updateType) {
            UpdateType.PERSONAL -> updatePersonalInfo(context)
            UpdateType.PASSWORD -> updatePassword(context)
        }
    }

    // 기본 프로필 이미지를 Bitmap으로 변환
    private fun getDefaultProfileBitmap(context: Context): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.base_profile)
    }

    // URL에서 Bitmap을 다운로드하는 함수
    private suspend fun downloadImageFromUrl(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection()
            connection.doInput = true
            connection.connect()
            val inputStream = connection.getInputStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 프로필 이미지를 Bitmap으로 변환하는 통합 함수
    private suspend fun getProfileBitmap(context: Context, currentState: UpdateInfoUiState): Bitmap {
        return if (currentState.isImageSelected) {
            // 새 이미지가 선택된 경우: URI에서 Bitmap 변환
            val imageUri = currentState.profileImageUri
            if (imageUri != null) {
                DefaultImageUtils.uriToBitmap(context, imageUri)
                    ?: getDefaultProfileBitmap(context)
            } else {
                getDefaultProfileBitmap(context)
            }
        } else {
            // 이미지가 변경되지 않은 경우: 기존 프로필 이미지 사용
            val currentProfileUrl = currentState.profileImageUri
            if (!currentProfileUrl.isNullOrEmpty() && currentProfileUrl.startsWith("http")) {
                // 서버에서 받은 URL이 있는 경우 다운로드해서 Bitmap으로 변환
                downloadImageFromUrl(currentProfileUrl) ?: getDefaultProfileBitmap(context)
            } else {
                // URL이 없거나 유효하지 않은 경우 기본 이미지 사용
                getDefaultProfileBitmap(context)
            }
        }
    }

    private fun updatePersonalInfo(context: Context) = intent {
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

        // 현재 state를 캡처해서 사용
        val currentState = state

        // 프로필 이미지 Bitmap 가져오기 (새 이미지 또는 기존 이미지)
        val profileBitmap = getProfileBitmap(context, currentState)

        runCatching {
            updateUserUseCase(
                password = currentState.password.text,
                nickname = currentState.newName.text,
                gender = currentState.gender.formatGender(),
                birthDate = currentState.birthDate.formatBirthDateToISO(),
                profileImage = profileBitmap
            )
        }.onSuccess { updatedUser ->
            reduce {
                state.copy(
                    isLoading = false,
                    name = updatedUser.nickname,
                    profileImageUri = updatedUser.profileUrl,
                    newName = TextFieldValue(updatedUser.nickname),
                    isImageSelected = false
                )
            }
            postSideEffect(UpdateInfoSideEffect.ProfileUpdated)
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ShowError(R.string.error_update_profile_failed))
        }
    }

    private fun updatePassword(context: Context) = intent {
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

        // 현재 state를 캡처해서 사용
        val currentState = state

        // 비밀번호 변경 시에도 프로필 이미지는 기존 것을 유지
        val profileBitmap = getProfileBitmap(context, currentState)

        runCatching {
            updateUserUseCase(
                password = currentState.newPassword.text,
                nickname = currentState.name,
                gender = currentState.gender.formatGender(),
                birthDate = currentState.birthDate.formatBirthDateToISO(),
                profileImage = profileBitmap
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
                    confirmPasswordError = null,
                    profileImageUri = updatedUser.profileUrl,
                    isImageSelected = false
                )
            }
            postSideEffect(UpdateInfoSideEffect.PasswordUpdated)
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(UpdateInfoSideEffect.ShowError(R.string.error_update_password_failed))
        }
    }
}
