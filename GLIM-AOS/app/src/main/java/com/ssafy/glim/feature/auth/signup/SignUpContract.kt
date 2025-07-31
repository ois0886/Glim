package com.ssafy.glim.feature.auth.signup

import androidx.annotation.StringRes
import androidx.compose.ui.text.input.TextFieldValue

data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.Email,
    val email: TextFieldValue = TextFieldValue(""),
    val code: TextFieldValue = TextFieldValue(""),
    val password: TextFieldValue = TextFieldValue(""),
    val confirmPassword: TextFieldValue = TextFieldValue(""),
    val name: TextFieldValue = TextFieldValue(""),
    val birthDate: TextFieldValue = TextFieldValue(""),
    val gender: String? = null,
    val actualVerificationCode: String = "",
    @StringRes val emailError: Int? = null,
    @StringRes val codeError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,
    @StringRes val nameError: Int? = null,
    @StringRes val birthDateError: Int? = null,
    val isLoading: Boolean = false,
    val showCelebrations: Boolean = false,
) {
    val isCurrentStepValid: Boolean
        get() = when (currentStep) {
            SignUpStep.Email -> email.text.isNotBlank() && emailError == null
            SignUpStep.Code -> code.text.isNotBlank() && codeError == null
            SignUpStep.Password ->
                password.text.isNotBlank() &&
                    confirmPassword.text.isNotBlank() &&
                    passwordError == null &&
                    confirmPasswordError == null

            SignUpStep.Profile ->
                name.text.isNotBlank() &&
                    birthDate.text.isNotBlank() &&
                    gender != null &&
                    nameError == null &&
                    birthDateError == null

            SignUpStep.Celebration -> true
        }
}

sealed interface SignUpSideEffect {
    data class ShowToast(@StringRes val message: Int) : SignUpSideEffect
}

enum class SignUpStep(val progress: Float) {
    Email(0.25f),
    Code(0.5f),
    Password(0.75f),
    Profile(1f),
    Celebration(0f);

    fun next(): SignUpStep? =
        when (this) {
            Email -> Code
            Code -> Password
            Password -> Profile
            Profile -> Celebration
            Celebration -> null
        }

    fun prev(): SignUpStep? =
        when (this) {
            Celebration -> Profile
            Profile -> Password
            Password -> Code
            Code -> Email
            Email -> null
        }
}
