package com.example.myapplication.feature.auth.login

import android.content.Context
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import com.example.myapplication.R
import com.example.myapplication.core.domain.usecase.auth.LoginUseCase
import com.example.myapplication.core.navigation.BottomTabRoute
import com.example.myapplication.core.navigation.Navigator
import com.example.myapplication.core.navigation.Route
import com.example.myapplication.feature.auth.login.component.SocialProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val navigator: Navigator,
    private val loginUseCase: LoginUseCase,
) : ViewModel(), ContainerHost<LoginUiState, LoginSideEffect> {
    companion object {
        private val PASSWORD_REGEX =
            Regex(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,16}$",
            )
    }

    override val container = container<LoginUiState, LoginSideEffect>(initialState = LoginUiState())

    fun onEmailChanged(email: String) =
        intent {
            val error = validateEmail(email)
            reduce { state.copy(email = email, emailError = error) }
        }

    fun onPasswordChanged(password: String) =
        intent {
            val error = validatePassword(password)
            reduce { state.copy(password = password, passwordError = error) }
        }

    fun onLoginClicked() =
        intent {
            val emailError = validateEmail(state.email)
            val passwordError = validatePassword(state.password)
            if (emailError != null || passwordError != null) {
                reduce { state.copy(emailError = emailError, passwordError = passwordError) }
                postSideEffect(LoginSideEffect.ShowError(emailError ?: passwordError!!))
                return@intent
            }
            reduce { state.copy(isLoading = true) }
            delay(1_000)
        }

    fun navigateToSignUp() = intent { navigator.navigate(Route.SignUp) }

    fun navigateToHome() = intent { navigator.navigate(BottomTabRoute.Home) }

    fun navigateToForgotPassword() = intent {}

    fun navigateToSocialLogin(socialProvider: SocialProvider) = intent {}

    private fun validateEmail(email: String): String? =
        when {
            email.isBlank() -> context.getString(R.string.error_email_empty)
            !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() ->
                context.getString(R.string.error_email_invalid)

            else -> null
        }

    private fun validatePassword(password: String): String? =
        when {
            password.isBlank() -> context.getString(R.string.error_password_empty)
            !PASSWORD_REGEX.matches(password) -> context.getString(R.string.error_password_invalid)
            else -> null
        }
}
