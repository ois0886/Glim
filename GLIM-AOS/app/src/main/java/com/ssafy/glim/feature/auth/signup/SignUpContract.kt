package com.ssafy.glim.feature.auth.signup

import androidx.annotation.StringRes
import androidx.compose.ui.text.input.TextFieldValue

data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.Terms,
    val allAgree: Boolean = false,
    val termsAgree: Boolean = false,
    val privacyAgree: Boolean = false,
    val marketingAgree: Boolean = false,
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
            SignUpStep.Terms -> termsAgree && privacyAgree
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
        }
}

sealed interface SignUpSideEffect {
    data class ShowToast(@StringRes val message: Int) : SignUpSideEffect
}

enum class SignUpStep(val progress: Float) {
    Terms(0.2f),
    Email(0.4f),
    Code(0.6f),
    Password(0.8f),
    Profile(1f);

    fun next(): SignUpStep? = when (this) {
        Terms -> Email
        Email -> Code
        Code -> Password
        Password -> Profile
        Profile -> null
    }

    fun prev(): SignUpStep? = when (this) {
        Profile -> Password
        Password -> Code
        Code -> Email
        Email -> Terms
        Terms -> null
    }
}
