package com.example.myapplication.feature.auth.login

import com.example.myapplication.feature.auth.login.component.SocialProvider

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
    data object NavigateMain : LoginSideEffect

    data object NavigateSignUp : LoginSideEffect

    data object NavigateForgotPassword : LoginSideEffect

    data class NavigateSocialLogin(val provider: SocialProvider) : LoginSideEffect

    data object NavigateGuest : LoginSideEffect

    data class ShowError(val message: String) : LoginSideEffect
}
