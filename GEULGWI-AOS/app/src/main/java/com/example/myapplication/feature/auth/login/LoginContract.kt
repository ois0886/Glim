package com.example.myapplication.feature.auth.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
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
    data class ShowError(val message: String) : LoginSideEffect
}
