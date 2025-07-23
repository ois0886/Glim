package com.ssafy.glim.feature.auth.signup

import androidx.annotation.StringRes

data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.Auth,
    val email: String = "",
    val code: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val birthDate: String = "",
    val gender: String? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val codeError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,
    @StringRes val nameError: Int? = null,
    @StringRes val birthDateError: Int? = null,
    val isLoading: Boolean = false,
) {
    val isCurrentStepValid: Boolean
        get() = when (currentStep) {
            SignUpStep.Auth -> email.isNotBlank() &&
                    emailError == null &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank() &&
                    passwordError == null &&
                    confirmPasswordError == null

            SignUpStep.Profile -> name.isNotBlank() &&
                    birthDate.isNotBlank() &&
                    gender != null &&
                    nameError == null &&
                    birthDateError == null

            SignUpStep.Code -> code.isNotBlank() &&
                    codeError == null
        }
}

sealed interface SignUpSideEffect {
    data class ShowToast(@StringRes val message: Int) : SignUpSideEffect
}

enum class SignUpStep(val progress: Float) {
    Auth(0.33f),
    Code(0.66f),
    Profile(1f);

    fun next(): SignUpStep? =
        when (this) {
            Auth -> Profile
            Profile -> Code
            Code -> null
        }

    fun prev(): SignUpStep? =
        when (this) {
            Code -> Profile
            Profile -> Auth
            Auth -> null
        }
}
