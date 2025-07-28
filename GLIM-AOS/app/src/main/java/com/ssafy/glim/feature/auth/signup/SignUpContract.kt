package com.ssafy.glim.feature.auth.signup

import androidx.annotation.StringRes

data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.Email,
    val email: String = "",
    val code: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val birthDate: String = "",
    val gender: String? = null,
    val actualVerificationCode: String = "",
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
            SignUpStep.Email -> email.isNotBlank() && emailError == null
            SignUpStep.Code -> code.isNotBlank() && codeError == null
            SignUpStep.Password ->
                password.isNotBlank() &&
                    confirmPassword.isNotBlank() &&
                    passwordError == null &&
                    confirmPasswordError == null

            SignUpStep.Profile ->
                name.isNotBlank() &&
                    birthDate.isNotBlank() &&
                    gender != null &&
                    nameError == null &&
                    birthDateError == null
        }
}

sealed interface SignUpSideEffect {
    data class ShowToast(@StringRes val message: Int) : SignUpSideEffect
}

enum class SignUpStep(val progress: Float) {
    Email(0.25f),
    Code(0.5f),
    Password(0.75f),
    Profile(1f);

    fun next(): SignUpStep? =
        when (this) {
            Email -> Code
            Code -> Password
            Password -> Profile
            Profile -> null
        }

    fun prev(): SignUpStep? =
        when (this) {
            Profile -> Password
            Password -> Code
            Code -> Email
            Email -> null
        }
}
