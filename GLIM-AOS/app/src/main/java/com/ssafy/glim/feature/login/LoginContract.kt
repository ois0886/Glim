package com.ssafy.glim.feature.login

import androidx.annotation.StringRes
import androidx.compose.ui.text.input.TextFieldValue

data class LoginUiState(
    val email: TextFieldValue = TextFieldValue(""),
    val password: TextFieldValue = TextFieldValue(""),
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    val isLoading: Boolean = false,
) {
    val isLoginEnabled: Boolean
        get() =
            emailError == null &&
                passwordError == null &&
                email.text.isNotBlank() &&
                password.text.isNotBlank()
}

sealed interface LoginSideEffect {
    data class ShowError(
        @StringRes val messageRes: Int,
    ) : LoginSideEffect
}
