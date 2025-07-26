package com.ssafy.glim.feature.auth.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.common.extensions.extractDigits
import com.ssafy.glim.core.common.extensions.formatBirthDate
import com.ssafy.glim.core.common.extensions.formatGender
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.common.utils.toErrorRes
import com.ssafy.glim.core.domain.usecase.auth.SignUpUseCase
import com.ssafy.glim.core.domain.usecase.auth.VerifyEmailUseCase
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class SignUpViewModel @Inject constructor(
    private val navigator: Navigator,
    private val signUpUseCase: SignUpUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase,
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

        if (filteredCode.length == 6 &&
            validationResult is ValidationResult.Valid &&
            filteredCode == state.actualVerificationCode
        ) {
            certifyValidCode()
        }
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
            SignUpStep.Email -> sendVerificationCode()
            SignUpStep.Code -> validateCodeStep()
            SignUpStep.Password -> validatePasswordStep()
            SignUpStep.Profile -> validateProfileStep()
        }
    }

    private fun sendVerificationCode() = intent {
        val validation = ValidationUtils.validateEmail(
            email = state.email,
            emptyErrorRes = R.string.error_email_empty,
            invalidErrorRes = R.string.error_email_invalid
        )

        when (validation) {
            is ValidationResult.Valid -> {
                reduce { state.copy(isLoading = true) }
                Log.d("SignUp", "Starting verification for email: ${state.email}")
                runCatching {
                    verifyEmailUseCase(state.email)
                }.onSuccess { response ->
                    Log.d("SignUp", "Verification success: ${response.verificationCode}")
                    reduce { state.copy(isLoading = false, actualVerificationCode = response.verificationCode) }
                    postSideEffect(SignUpSideEffect.ShowToast(R.string.verification_code_instruction))
                    moveToNextStep()
                }.onFailure { exception ->
                    Log.e("SignUp", "Verification failed: ${exception.message}", exception)
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(SignUpSideEffect.ShowToast(R.string.verification_code_failed))
                    moveToNextStep()
                }
            }
            is ValidationResult.Invalid -> handleEmailValidationError(validation.errorMessageRes)
        }
    }

    private fun handleEmailValidationError(errorRes: Int) = intent {
        postSideEffect(SignUpSideEffect.ShowToast(errorRes))
        reduce { state.copy(emailError = errorRes) }
    }

    private fun validateCodeStep() = intent {
        val validation = ValidationUtils.validateCode(
            code = state.code,
            emptyErrorRes = R.string.error_code_empty,
            invalidErrorRes = R.string.error_code_invalid
        )

        when (validation) {
            is ValidationResult.Valid -> certifyValidCode()
            is ValidationResult.Invalid -> handleCodeValidationError(validation.errorMessageRes)
        }
    }

    private fun handleCodeValidationError(errorRes: Int) = intent {
        postSideEffect(SignUpSideEffect.ShowToast(errorRes))
        reduce { state.copy(codeError = errorRes) }
    }

    private fun certifyValidCode() = intent {
        if (state.code == state.actualVerificationCode) {
            postSideEffect(SignUpSideEffect.ShowToast(R.string.signup_verify_code))
            moveToNextStep()
        } else {
            postSideEffect(SignUpSideEffect.ShowToast(R.string.error_code_incorrect))
            reduce { state.copy(codeError = R.string.error_code_incorrect) }
            moveToNextStep()
        }
    }

    private fun validatePasswordStep() = intent {
        val passwordValidation = ValidationUtils.validatePassword(
            password = state.password,
            emptyErrorRes = R.string.error_password_empty,
            invalidErrorRes = R.string.error_password_invalid
        )

        val confirmValidation = ValidationUtils.validatePasswordConfirm(
            password = state.password,
            confirmPassword = state.confirmPassword,
            mismatchErrorRes = R.string.error_password_mismatch
        )

        val passwordError = passwordValidation.toErrorRes()
        val confirmError = confirmValidation.toErrorRes()

        if (passwordError != null || confirmError != null) {
            handlePasswordValidationErrors(passwordError, confirmError)
        } else {
            moveToNextStep()
        }
    }

    private fun handlePasswordValidationErrors(
        passwordError: Int?,
        confirmError: Int?
    ) = intent {
        val firstError = passwordError ?: confirmError!!
        postSideEffect(SignUpSideEffect.ShowToast(firstError))
        reduce {
            state.copy(
                passwordError = passwordError,
                confirmPasswordError = confirmError,
            )
        }
    }

    private fun validateProfileStep() = intent {
        val nameValidation = ValidationUtils.validateName(
            name = state.name,
            emptyErrorRes = R.string.error_name_empty,
            invalidErrorRes = R.string.error_name_invalid
        )

        val birthDateValidation = ValidationUtils.validateBirthDate(
            birthDate = state.birthDate,
            emptyErrorRes = R.string.error_birth_empty,
            formatErrorRes = R.string.error_birth_format,
            yearErrorRes = R.string.error_birth_year,
            monthErrorRes = R.string.error_birth_month,
            dayErrorRes = R.string.error_birth_day,
            futureDateErrorRes = R.string.error_birth_future
        )

        val genderValidation = ValidationUtils.validateGender(
            gender = state.gender,
            emptyErrorRes = R.string.error_gender_empty
        )

        val nameError = nameValidation.toErrorRes()
        val birthDateError = birthDateValidation.toErrorRes()
        val genderError = genderValidation.toErrorRes()

        if (nameError != null || birthDateError != null || genderError != null) {
            handleProfileValidationErrors(nameError, birthDateError, genderError)
        } else {
            performSignUp()
        }
    }

    private fun handleProfileValidationErrors(
        nameError: Int?,
        birthDateError: Int?,
        genderError: Int?
    ) = intent {
        val firstError = nameError ?: birthDateError ?: genderError!!
        postSideEffect(SignUpSideEffect.ShowToast(firstError))
        reduce {
            state.copy(
                nameError = nameError,
                birthDateError = birthDateError,
            )
        }
    }

    fun onBackStep() = intent {
        state.currentStep.prev()?.let { prev ->
            reduce { state.copy(currentStep = prev) }
        } ?: navigator.navigateBack()
    }

    private fun moveToNextStep() = intent {
        state.currentStep.next()?.let { next ->
            reduce { state.copy(currentStep = next) }
        }
    }

    private fun performSignUp() = intent {
        val formattedBirthDate = state.birthDate.formatBirthDate()
        val genderData = checkNotNull(state.gender) { "Data must not be null at this point" }
        val formattedGender = genderData.formatGender()

        reduce { state.copy(isLoading = true) }

        runCatching {
            signUpUseCase(
                email = state.email,
                nickname = state.name,
                password = state.password,
                gender = formattedGender,
                birthDate = formattedBirthDate
            )
        }.onSuccess {
            reduce { state.copy(isLoading = false) }
            postSideEffect(SignUpSideEffect.ShowToast(R.string.signup_success))
            navigator.navigate(route = Route.Login, launchSingleTop = true)
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(SignUpSideEffect.ShowToast(R.string.signup_failed))
        }
    }
}
