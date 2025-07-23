package com.ssafy.glim.feature.auth.login

import androidx.annotation.StringRes

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    val isLoading: Boolean = false,
) {
    val isLoginEnabled: Boolean
        get() =
            emailError == null &&
                    passwordError == null &&
                    email.isNotBlank() &&
                    password.isNotBlank()
}

sealed interface LoginSideEffect {
    data class ShowError(@StringRes val messageRes: Int) : LoginSideEffect
}