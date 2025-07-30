package com.ssafy.glim.feature.auth.signup

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
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

    fun onEmailChanged(email: TextFieldValue) = intent {
        val validationResult = ValidationUtils.validateEmail(
            email = email.text,
            emptyErrorRes = R.string.error_email_empty,
            invalidErrorRes = R.string.error_email_invalid
        )

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(email = email, emailError = error) }
    }

    fun onCodeChanged(code: TextFieldValue) = intent {
        val filteredCode = code.text.extractDigits(6)

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

        reduce { state.copy(code = TextFieldValue(filteredCode), codeError = error) }
    }

    fun onPasswordChanged(password: TextFieldValue) = intent {
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

        val confirmValidation = ValidationUtils.validatePasswordConfirm(
            password = password.text,
            confirmPassword = state.confirmPassword.text,
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

    fun onConfirmPasswordChanged(confirmPassword: TextFieldValue) = intent {
        val validationResult = ValidationUtils.validatePasswordConfirm(
            password = state.password.text,
            confirmPassword = confirmPassword.text,
            mismatchErrorRes = R.string.error_password_mismatch
        )

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(confirmPassword = confirmPassword, confirmPasswordError = error) }
    }

    fun onNameChanged(name: TextFieldValue) = intent {
        val validationResult = if (name.text.isNotBlank()) {
            ValidationUtils.validateName(
                name = name.text,
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
            email = state.email.text,
            emptyErrorRes = R.string.error_email_empty,
            invalidErrorRes = R.string.error_email_invalid
        )

        val emailError = validation.toErrorRes()
        reduce { state.copy(emailError = emailError) }

        when (validation) {
            is ValidationResult.Valid -> {
                reduce { state.copy(isLoading = true) }
                Log.d("SignUp", "Starting verification for email: ${state.email}")
                runCatching {
                    verifyEmailUseCase(state.email.text)
                }.onSuccess { response ->
                    Log.d("SignUp", "Verification success: ${response.verificationCode}")
                    reduce { state.copy(isLoading = false, actualVerificationCode = response.verificationCode) }
                    postSideEffect(SignUpSideEffect.ShowToast(R.string.verification_code_instruction))
                    moveToNextStep()
                }.onFailure { exception ->
                    Log.e("SignUp", "Verification failed: ${exception.message}", exception)
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(SignUpSideEffect.ShowToast(R.string.verification_code_failed))
                }
            }

            is ValidationResult.Invalid -> {
                postSideEffect(SignUpSideEffect.ShowToast(validation.errorMessageRes))
            }
        }
    }

    private fun validateCodeStep() = intent {
        val validation = ValidationUtils.validateCode(
            code = state.code.text,
            emptyErrorRes = R.string.error_code_empty,
            invalidErrorRes = R.string.error_code_invalid
        )

        val codeError = if (validation is ValidationResult.Valid) {
            if (state.code.text == state.actualVerificationCode) {
                null
            } else {
                R.string.error_code_incorrect
            }
        } else {
            validation.toErrorRes()
        }

        reduce { state.copy(codeError = codeError) }

        when (validation) {
            is ValidationResult.Valid -> {
                if (state.code.text == state.actualVerificationCode) {
                    postSideEffect(SignUpSideEffect.ShowToast(R.string.signup_verify_code))
                    moveToNextStep()
                } else {
                    postSideEffect(SignUpSideEffect.ShowToast(R.string.error_code_incorrect))
                }
            }

            is ValidationResult.Invalid -> {
                postSideEffect(SignUpSideEffect.ShowToast(validation.errorMessageRes))
            }
        }
    }

    private fun validatePasswordStep() = intent {
        val passwordValidation = ValidationUtils.validatePassword(
            password = state.password.text,
            emptyErrorRes = R.string.error_password_empty,
            invalidErrorRes = R.string.error_password_invalid
        )

        val confirmValidation = ValidationUtils.validatePasswordConfirm(
            password = state.password.text,
            confirmPassword = state.confirmPassword.text,
            mismatchErrorRes = R.string.error_password_mismatch
        )

        val passwordError = passwordValidation.toErrorRes()
        val confirmError = confirmValidation.toErrorRes()

        reduce {
            state.copy(
                passwordError = passwordError,
                confirmPasswordError = confirmError,
            )
        }

        if (passwordError != null || confirmError != null) {
            val firstError = passwordError ?: confirmError!!
            postSideEffect(SignUpSideEffect.ShowToast(firstError))
        } else {
            moveToNextStep()
        }
    }

    private fun validateProfileStep() = intent {
        val nameValidation = ValidationUtils.validateName(
            name = state.name.text,
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

        reduce {
            state.copy(
                nameError = nameError,
                birthDateError = birthDateError,
            )
        }

        if (nameError != null || birthDateError != null || genderError != null) {
            val firstError = nameError ?: birthDateError ?: genderError!!
            postSideEffect(SignUpSideEffect.ShowToast(firstError))
        } else {
            performSignUp()
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
                email = state.email.text,
                nickname = state.name.text,
                password = state.password.text,
                gender = formattedGender,
                birthDate = formattedBirthDate
            )
        }.onSuccess {
            reduce { state.copy(isLoading = false) }
            postSideEffect(SignUpSideEffect.ShowToast(R.string.signup_success))
            navigator.navigateAndClearBackStack(Route.Login)
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(SignUpSideEffect.ShowToast(R.string.signup_failed))
        }
    }
}
