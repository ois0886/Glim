package com.example.myapplication.feature.auth.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.core.ui.GlimTopBar
import com.example.myapplication.core.ui.TitleAlignment
import com.example.myapplication.feature.auth.login.component.EmailInputTextField
import com.example.myapplication.feature.auth.login.component.GlimButton
import com.example.myapplication.feature.auth.login.component.PasswordInputTextField
import com.example.myapplication.feature.auth.login.component.SocialButton
import com.example.myapplication.feature.auth.login.component.SocialProvider
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * LoginRoute: ViewModel SideEffect 및 State 구독 → 네비게이션/토스트 처리
 */
@Composable
internal fun LoginRoute(
    padding: PaddingValues,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is LoginSideEffect.ShowError ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()

            else -> viewModel.navigate(effect)
        }
    }

    LoginScreen(
        state = uiState,
        padding = padding,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onLoginClicked = viewModel::onLoginClicked,
        onSignUpClicked = viewModel::onSignUpClicked,
        onForgotPassword = viewModel::onForgotPasswordClicked,
        onSocialLogin = viewModel::onSocialLoginClicked,
        onGuest = viewModel::onGuestClicked,
    )
}

/**
 * LoginScreen: UI 그리기
 */
@Composable
internal fun LoginScreen(
    state: LoginUiState,
    padding: PaddingValues,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    onForgotPassword: () -> Unit,
    onSocialLogin: (SocialProvider) -> Unit,
    onGuest: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GlimTopBar(
            title = stringResource(id = R.string.login_title),
            showBack = false,
            alignment = TitleAlignment.Center,
            titleColor = Color.Black,
            titleSize = 20.sp,
        )

        Spacer(Modifier.height(48.dp))

        EmailInputTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            error = state.emailError,
        )

        Spacer(Modifier.height(16.dp))

        PasswordInputTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            error = state.passwordError,
        )

        Spacer(Modifier.height(24.dp))

        GlimButton(
            text =
                if (state.isLoading) {
                    stringResource(R.string.login_loading)
                } else {
                    stringResource(R.string.login_button)
                },
            onClick = onLoginClicked,
            enabled = state.isLoginEnabled && !state.isLoading,
        )

        Spacer(Modifier.height(12.dp))
        Row {
            TextButton(onClick = onSignUpClicked) {
                Text(stringResource(id = R.string.login_signup))
            }
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = onForgotPassword) {
                Text(stringResource(id = R.string.login_forgot_password))
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                stringResource(R.string.login_sns_title),
                style = MaterialTheme.typography.bodySmall,
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SocialButton(SocialProvider.GOOGLE) { onSocialLogin(SocialProvider.GOOGLE) }
            SocialButton(SocialProvider.KAKAO) { onSocialLogin(SocialProvider.KAKAO) }
            SocialButton(SocialProvider.NAVER) { onSocialLogin(SocialProvider.NAVER) }
        }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onGuest) {
            Text(stringResource(R.string.login_guest))
        }
    }
}

// Previews

@Preview(name = "Empty Form", showBackground = true)
@Composable
fun PreviewLoginScreen_Empty() {
    LoginScreen(
        state = LoginUiState(),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onSignUpClicked = {},
        onForgotPassword = {},
        onSocialLogin = {},
        onGuest = {},
    )
}

@Preview(name = "With Errors", showBackground = true)
@Composable
fun PreviewLoginScreen_Errors() {
    LoginScreen(
        state =
            LoginUiState(
                email = "invalid-email",
                password = "short",
                emailError = "유효한 이메일 형식을 입력해주세요.",
                passwordError = "8~16자, 영문 대/소문자·숫자·특수문자 포함",
            ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onSignUpClicked = {},
        onForgotPassword = {},
        onSocialLogin = {},
        onGuest = {},
    )
}

@Preview(name = "Valid Input", showBackground = true)
@Composable
fun PreviewLoginScreen_Valid() {
    LoginScreen(
        state =
            LoginUiState(
                email = "user@example.com",
                password = "Aa1!abcd",
            ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onSignUpClicked = {},
        onForgotPassword = {},
        onSocialLogin = {},
        onGuest = {},
    )
}

@Preview(name = "Loading State", showBackground = true)
@Composable
fun PreviewLoginScreen_Loading() {
    LoginScreen(
        state =
            LoginUiState(
                email = "user@example.com",
                password = "Aa1!abcd",
                isLoading = true,
            ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onSignUpClicked = {},
        onForgotPassword = {},
        onSocialLogin = {},
        onGuest = {},
    )
}
