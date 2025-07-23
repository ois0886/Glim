package com.ssafy.glim.feature.auth.signup

import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.common.extensions.extractDigits
import com.ssafy.glim.core.common.extensions.formatBirthDate
import com.ssafy.glim.core.common.extensions.formatGender
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.common.utils.toErrorRes
import com.ssafy.glim.core.domain.usecase.auth.CertifyValidCodeUseCase
import com.ssafy.glim.core.domain.usecase.auth.SignUpUseCase
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
internal class SignUpViewModel
@Inject
constructor(
    private val navigator: Navigator,
    private val signUpUseCase: SignUpUseCase,
    private val certifyValidCodeUseCase: CertifyValidCodeUseCase,
) : ViewModel(), ContainerHost<SignUpUiState, SignUpSideEffect> {

    override val container = container<SignUpUiState, SignUpSideEffect>(SignUpUiState())

    fun onEmailChanged(email: String) = intent {
        val validationResult = if (email.isNotBlank()) {
            ValidationUtils.validateEmail(
                email = email,
                emptyErrorRes = R.string.error_email_empty,
                invalidErrorRes = R.string.error_email_invalid
            )
        } else {
            ValidationResult.Valid
        }

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(email = email, emailError = error) }
    }

    fun onCodeChanged(code: String) = intent {
        val filteredCode = code.extractDigits(6)

        val validationResult = if (filteredCode.isNotBlank()) {
            ValidationUtils.validateCode(
                code = filteredCode,
                emptyErrorRes = R.string.error_code_empty,
                invalidErrorRes = R.string.error_code_invalid
            )
        } else {
            ValidationResult.Valid
        }

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(code = filteredCode, codeError = error) }
    }

    fun onPasswordChanged(password: String) = intent {
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

        val confirmValidation = ValidationUtils.validatePasswordConfirm(
            password = password,
            confirmPassword = state.confirmPassword,
            mismatchErrorRes = R.string.error_password_mismatch
        )

        val confirmError = when (confirmValidation) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> confirmValidation.errorMessageRes
        }

        reduce {
            state.copy(
                password = password,
                passwordError = passwordError,
                confirmPasswordError = confirmError,
            )
        }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) = intent {
        val validationResult = ValidationUtils.validatePasswordConfirm(
            password = state.password,
            confirmPassword = confirmPassword,
            mismatchErrorRes = R.string.error_password_mismatch
        )

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(confirmPassword = confirmPassword, confirmPasswordError = error) }
    }

    fun onNameChanged(name: String) = intent {
        val validationResult = if (name.isNotBlank()) {
            ValidationUtils.validateName(
                name = name,
                emptyErrorRes = R.string.error_name_empty,
                invalidErrorRes = R.string.error_name_invalid
            )
        } else {
            ValidationResult.Valid
        }

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(name = name, nameError = error) }
    }

    fun onBirthChanged(birth: String) = intent {
        val filteredBirth = birth.extractDigits(8)

        val validationResult = if (filteredBirth.isNotBlank()) {
            ValidationUtils.validateBirthDate(
                birthDate = filteredBirth,
                emptyErrorRes = R.string.error_birth_empty,
                formatErrorRes = R.string.error_birth_format,
                yearErrorRes = R.string.error_birth_year,
                monthErrorRes = R.string.error_birth_month,
                dayErrorRes = R.string.error_birth_day,
                futureDateErrorRes = R.string.error_birth_future
            )
        } else {
            ValidationResult.Valid
        }

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(birthDate = filteredBirth, birthDateError = error) }
    }

    fun onGenderSelected(gender: String) = intent {
        reduce { state.copy(gender = gender) }
    }

    fun onNextStep() = intent {
        when (state.currentStep) {
            SignUpStep.Auth -> validateAuthStep()
            SignUpStep.Profile -> validateProfileStep()
            SignUpStep.Code -> validateCodeStep()
        }
    }

    private fun validateAuthStep() = intent {
        val emailError = ValidationUtils.validateEmail(
            email = state.email,
            emptyErrorRes = R.string.error_email_empty,
            invalidErrorRes = R.string.error_email_invalid
        ).toErrorRes()

        val passwordError = ValidationUtils.validatePassword(
            password = state.password,
            emptyErrorRes = R.string.error_password_empty,
            invalidErrorRes = R.string.error_password_invalid
        ).toErrorRes()

        val confirmError = ValidationUtils.validatePasswordConfirm(
            password = state.password,
            confirmPassword = state.confirmPassword,
            mismatchErrorRes = R.string.error_password_mismatch
        ).toErrorRes()

        if (emailError != null || passwordError != null || confirmError != null) {
            showAuthErrors(emailError, passwordError, confirmError)
        } else {
            moveToNextStep()
        }
    }

    private fun validateProfileStep() = intent {
        val nameError = ValidationUtils.validateName(
            name = state.name,
            emptyErrorRes = R.string.error_name_empty,
            invalidErrorRes = R.string.error_name_invalid
        ).toErrorRes()

        val birthDateError = ValidationUtils.validateBirthDate(
            birthDate = state.birthDate,
            emptyErrorRes = R.string.error_birth_empty,
            formatErrorRes = R.string.error_birth_format,
            yearErrorRes = R.string.error_birth_year,
            monthErrorRes = R.string.error_birth_month,
            dayErrorRes = R.string.error_birth_day,
            futureDateErrorRes = R.string.error_birth_future
        ).toErrorRes()

        val genderError = ValidationUtils.validateGender(
            gender = state.gender,
            emptyErrorRes = R.string.error_gender_empty
        ).toErrorRes()

        if (nameError != null || birthDateError != null || genderError != null) {
            showProfileErrors(nameError, birthDateError, genderError)
        } else {
            moveToNextStep()
        }
    }

    private fun validateCodeStep() = intent {
        val codeValidation = ValidationUtils.validateCode(
            code = state.code,
            emptyErrorRes = R.string.error_code_empty,
            invalidErrorRes = R.string.error_code_invalid
        )

        when (codeValidation) {
            is ValidationResult.Valid -> certifyValidCode()
            is ValidationResult.Invalid -> showCodeError(codeValidation.errorMessageRes)
        }
    }

    private fun showAuthErrors(
        emailError: Int?,
        passwordError: Int?,
        confirmError: Int?
    ) = intent {
        val firstError = emailError ?: passwordError ?: confirmError!!
        postSideEffect(SignUpSideEffect.ShowToast(firstError))

        reduce {
            state.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmError
            )
        }
    }

    private fun showProfileErrors(
        nameError: Int?,
        birthDateError: Int?,
        genderError: Int?
    ) = intent {
        val firstError = nameError ?: birthDateError ?: genderError!!
        postSideEffect(SignUpSideEffect.ShowToast(firstError))

        reduce {
            state.copy(
                nameError = nameError,
                birthDateError = birthDateError
            )
        }
    }

    private fun showCodeError(errorRes: Int) = intent {
        postSideEffect(SignUpSideEffect.ShowToast(errorRes))
        reduce {
            state.copy(codeError = errorRes)
        }
    }

    fun onBackStep() = intent {
        state.currentStep.prev()?.let { prev ->
            reduce { state.copy(currentStep = prev) }
        } ?: navigator.navigateBack()
    }

    private fun certifyValidCode() = intent {
        performSignUp()
        // TODO: 실제 인증 코드 검증 로직
        /*
        certifyValidCodeUseCase(state.code)
            .onStart {
                reduce { state.copy(isLoading = true) }
            }
            .catch { exception ->
                reduce { state.copy(isLoading = false) }
                val errorMessage = exception.message ?: "코드 인증에 실패했습니다."
                postSideEffect(SignUpSideEffect.ShowToast(R.string.error_code_verification_failed))
                reduce { state.copy(codeError = R.string.error_code_verification_failed) }
            }
            .collect { result ->
                reduce { state.copy(isLoading = false) }
                if (result.isSuccess) {
                    moveToNextStep()
                } else {
                    postSideEffect(SignUpSideEffect.ShowToast(R.string.error_code_incorrect))
                    reduce { state.copy(codeError = R.string.error_code_incorrect) }
                }
            }
         */
    }

    private fun moveToNextStep() = intent {
        state.currentStep.next()?.let { next ->
            reduce { state.copy(currentStep = next) }
        }
    }

    private fun performSignUp() = intent {
        val formattedBirthDate = state.birthDate.formatBirthDate()
        val genderData = checkNotNull(state.gender) { "Data must not be null at this point" }
        genderData.formatGender()

        signUpUseCase(
            email = state.email,
            nickname = state.name,
            password = state.password,
            gender = genderData,
            birthDate = formattedBirthDate
        )
            .onStart {
                reduce { state.copy(isLoading = true) }
            }
            .catch { exception ->
                reduce { state.copy(isLoading = false) }
                postSideEffect(SignUpSideEffect.ShowToast(R.string.signup_failed))
            }
            .collect { result ->
                reduce { state.copy(isLoading = false) }
                if (result.isSuccess) {
                    postSideEffect(SignUpSideEffect.ShowToast(R.string.signup_success))
                    navigator.navigate(route = Route.Login, launchSingleTop = true)
                } else {
                    postSideEffect(SignUpSideEffect.ShowToast(R.string.signup_failed))
                }
            }
    }
}
