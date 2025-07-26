package com.ssafy.glim.feature.auth.login

import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.domain.usecase.auth.LoginUseCase
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.auth.login.component.SocialProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel
@Inject
constructor(
    private val navigator: Navigator,
    private val loginUseCase: LoginUseCase,
) : ViewModel(), ContainerHost<LoginUiState, LoginSideEffect> {
    override val container = container<LoginUiState, LoginSideEffect>(initialState = LoginUiState())

    fun onEmailChanged(email: String) =
        intent {
            val validationResult =
                ValidationUtils.validateEmail(
                    email = email,
                    emptyErrorRes = R.string.error_email_empty,
                    invalidErrorRes = R.string.error_email_invalid,
                )

            val error =
                when (validationResult) {
                    is ValidationResult.Valid -> null
                    is ValidationResult.Invalid -> validationResult.errorMessageRes
                }

            reduce { state.copy(email = email, emailError = error) }
        }

    fun onPasswordChanged(password: String) =
        intent {
            val validationResult =
                ValidationUtils.validatePassword(
                    password = password,
                    emptyErrorRes = R.string.error_password_empty,
                    invalidErrorRes = R.string.error_password_invalid,
                )

            val error =
                when (validationResult) {
                    is ValidationResult.Valid -> null
                    is ValidationResult.Invalid -> validationResult.errorMessageRes
                }

            reduce { state.copy(password = password, passwordError = error) }
        }

    fun onLoginClicked() = intent {
        val emailValidation = ValidationUtils.validateEmail(
            email = state.email,
            emptyErrorRes = R.string.error_email_empty,
            invalidErrorRes = R.string.error_email_invalid,
        )

        val passwordValidation = ValidationUtils.validatePassword(
            password = state.password,
            emptyErrorRes = R.string.error_password_empty,
            invalidErrorRes = R.string.error_password_invalid,
        )

        val emailError = if (emailValidation is ValidationResult.Invalid) {
            emailValidation.errorMessageRes
        } else {
            null
        }

        val passwordError = if (passwordValidation is ValidationResult.Invalid) {
            passwordValidation.errorMessageRes
        } else {
            null
        }

        if (emailError != null || passwordError != null) {
            reduce { state.copy(emailError = emailError, passwordError = passwordError) }
            postSideEffect(LoginSideEffect.ShowError(emailError ?: passwordError!!))
            return@intent
        }

        reduce { state.copy(isLoading = true) }

        runCatching {
            loginUseCase(
                email = state.email,
                password = state.password,
            )
        }.onSuccess {
            reduce { state.copy(isLoading = false) }
            navigateToHome()
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(LoginSideEffect.ShowError(R.string.login_failed))
        }
    }

    fun navigateToSignUp() =
        intent {
            navigator.navigate(Route.SignUp)
        }

    fun navigateToHome() =
        intent {
            navigator.navigate(BottomTabRoute.Home)
        }

    fun navigateToForgotPassword() =
        intent {
            // TODO: 비밀번호 찾기 화면으로 이동
        }

    fun navigateToSocialLogin(socialProvider: SocialProvider) =
        intent {
            // TODO: 소셜 로그인 구현
            when (socialProvider) {
                SocialProvider.GOOGLE -> {
                    postSideEffect(LoginSideEffect.ShowError(R.string.social_login_message))
                }

                SocialProvider.KAKAO -> {
                    postSideEffect(LoginSideEffect.ShowError(R.string.social_login_message))
                }

                SocialProvider.NAVER -> {
                    postSideEffect(LoginSideEffect.ShowError(R.string.social_login_message))
                }
            }
        }
}
