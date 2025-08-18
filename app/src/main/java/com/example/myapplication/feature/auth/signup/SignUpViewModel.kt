package com.example.myapplication.feature.auth.signup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
internal class SignUpViewModel
    @Inject
    constructor() :
    ViewModel(), ContainerHost<SignUpUiState, SignUpSideEffect> {
        override val container = container<SignUpUiState, SignUpSideEffect>(SignUpUiState())

        fun onEmailChanged(email: String) =
            intent {
                reduce { state.copy(email = email, emailError = null) }
            }

        fun onCodeChanged(code: String) =
            intent {
                reduce { state.copy(code = code, codeError = null) }
            }

        fun onPasswordChanged(password: String) =
            intent {
                reduce { state.copy(password = password, passwordError = null) }
            }

        fun onConfirmPasswordChanged(confirmPassword: String) =
            intent {
                reduce { state.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
            }

        fun onNameChanged(name: String) =
            intent {
                reduce { state.copy(name = name) }
            }

        fun onBirthYearChanged(birthYear: String) =
            intent {
                reduce { state.copy(birthYear = birthYear) }
            }

        fun onGenderSelected(gender: String) =
            intent {
                reduce { state.copy(gender = gender) }
            }

        fun onNextStep() =
            intent {
                when (state.currentStep) {
                    SignUpStep.Email -> {
                        if (state.email.isBlank()) {
                            postSideEffect(SignUpSideEffect.ShowToast("이메일을 입력해주세요."))
                            reduce { state.copy(emailError = "이메일을 입력해주세요.") }
                            return@intent
                        }
                    }

                    SignUpStep.Code -> {
                        if (state.code.isBlank()) {
                            postSideEffect(SignUpSideEffect.ShowToast("인증 코드를 입력해주세요."))
                            reduce { state.copy(codeError = "인증 코드를 입력해주세요.") }
                            return@intent
                        }
                    }

                    SignUpStep.Password -> {
                        if (state.password.isBlank()) {
                            postSideEffect(SignUpSideEffect.ShowToast("비밀번호를 입력해주세요."))
                            reduce { state.copy(passwordError = "비밀번호를 입력해주세요.") }
                            return@intent
                        }
                        if (state.password != state.confirmPassword) {
                            postSideEffect(SignUpSideEffect.ShowToast("비밀번호가 일치하지 않습니다."))
                            reduce { state.copy(confirmPasswordError = "비밀번호가 일치하지 않습니다.") }
                            return@intent
                        }
                    }

                    SignUpStep.Profile -> {
                        reduce { state.copy(isLoading = true) }
                        delay(1_000)
                        postSideEffect(SignUpSideEffect.NavigateToMain)
                        return@intent
                    }
                }
                state.currentStep.next()?.let { next ->
                    reduce { state.copy(currentStep = next) }
                }
            }

        fun onBackStep() =
            intent {
                state.currentStep.prev()?.let { prev ->
                    reduce { state.copy(currentStep = prev) }
                }
            }
    }
