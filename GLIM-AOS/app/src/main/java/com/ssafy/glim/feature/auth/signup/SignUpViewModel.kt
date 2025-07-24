package com.ssafy.glim.feature.auth.signup

import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.common.extensions.extractDigits
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.domain.usecase.auth.CertifyValidCodeUseCase
import com.ssafy.glim.core.domain.usecase.auth.SignUpUseCase
import com.ssafy.glim.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
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

        reduce { state.copy(birthYear = filteredBirth, birthYearError = error) }
    }

    fun onGenderSelected(gender: String) = intent {
        reduce { state.copy(gender = gender) }
    }

    fun onNextStep() = intent {
        when (state.currentStep) {
            SignUpStep.Email -> {
                val validation = ValidationUtils.validateEmail(
                    email = state.email,
                    emptyErrorRes = R.string.error_email_empty,
                    invalidErrorRes = R.string.error_email_invalid
                )

                when (validation) {
                    is ValidationResult.Valid -> moveToNextStep()
                    is ValidationResult.Invalid -> {
                        postSideEffect(SignUpSideEffect.ShowToast(validation.errorMessageRes))
                        reduce { state.copy(emailError = validation.errorMessageRes) }
                    }
                }
            }

            SignUpStep.Code -> {
                val validation = ValidationUtils.validateCode(
                    code = state.code,
                    emptyErrorRes = R.string.error_code_empty,
                    invalidErrorRes = R.string.error_code_invalid
                )

                when (validation) {
                    is ValidationResult.Valid -> certifyValidCode()
                    is ValidationResult.Invalid -> {
                        postSideEffect(SignUpSideEffect.ShowToast(validation.errorMessageRes))
                        reduce { state.copy(codeError = validation.errorMessageRes) }
                    }
                }
            }

            SignUpStep.Password -> {
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

                val passwordError = when (passwordValidation) {
                    is ValidationResult.Valid -> null
                    is ValidationResult.Invalid -> passwordValidation.errorMessageRes
                }

                val confirmError = when (confirmValidation) {
                    is ValidationResult.Valid -> null
                    is ValidationResult.Invalid -> confirmValidation.errorMessageRes
                }

                if (passwordError != null || confirmError != null) {
                    val errorRes = passwordError ?: confirmError!!
                    postSideEffect(SignUpSideEffect.ShowToast(errorRes))
                    reduce {
                        state.copy(
                            passwordError = passwordError,
                            confirmPasswordError = confirmError,
                        )
                    }
                } else {
                    moveToNextStep()
                }
            }

            SignUpStep.Profile -> {
                val nameValidation = ValidationUtils.validateName(
                    name = state.name,
                    emptyErrorRes = R.string.error_name_empty,
                    invalidErrorRes = R.string.error_name_invalid
                )

                val birthDateValidation = ValidationUtils.validateBirthDate(
                    birthDate = state.birthYear,
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

                val nameError = when (nameValidation) {
                    is ValidationResult.Valid -> null
                    is ValidationResult.Invalid -> nameValidation.errorMessageRes
                }

                val birthDateError = when (birthDateValidation) {
                    is ValidationResult.Valid -> null
                    is ValidationResult.Invalid -> birthDateValidation.errorMessageRes
                }

                val genderError = when (genderValidation) {
                    is ValidationResult.Valid -> null
                    is ValidationResult.Invalid -> genderValidation.errorMessageRes
                }

                if (nameError != null || birthDateError != null || genderError != null) {
                    val errorRes = nameError ?: birthDateError ?: genderError!!
                    postSideEffect(SignUpSideEffect.ShowToast(errorRes))
                    reduce {
                        state.copy(
                            nameError = nameError,
                            birthYearError = birthDateError,
                        )
                    }
                } else {
                    // TODO: 회원가입 완료 처리
                }
            }
        }
    }

    fun onBackStep() = intent {
        state.currentStep.prev()?.let { prev ->
            reduce { state.copy(currentStep = prev) }
        } ?: navigator.navigateBack()
    }

    private fun certifyValidCode() = intent {
        moveToNextStep()

        // TODO: 실제 인증 코드 검증 로직
        /*
        certifyValidCodeUseCase(state.code)
            .onStart {
                reduce { state.copy(isLoading = true) }
            }
            .catch { exception ->
                reduce { state.copy(isLoading = false) }
                val errorMessage = exception.message ?: "코드 인증에 실패했습니다."
                postSideEffect(SignUpSideEffect.ShowToast(errorMessage))
                reduce { state.copy(codeError = R.string.error_code_verification_failed) }
            }
            .collect { result ->
                reduce { state.copy(isLoading = false) }
                if (result.isSuccess) {
                    moveToNextStep()
                } else {
                    postSideEffect(SignUpSideEffect.ShowToastRes(R.string.error_code_incorrect))
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
}
