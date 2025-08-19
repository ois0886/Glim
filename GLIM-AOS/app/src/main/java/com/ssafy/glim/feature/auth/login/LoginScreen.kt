package com.ssafy.glim.feature.auth.login

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.feature.auth.login.component.EmailInputTextField
import com.ssafy.glim.feature.auth.login.component.GlimButton
import com.ssafy.glim.feature.auth.login.component.PasswordInputTextField
import com.ssafy.glim.ui.theme.caveatFont
import org.orbitmvi.orbit.compose.collectSideEffect

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
                Toast.makeText(context, context.getString(effect.messageRes), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    LoginScreen(
        state = uiState,
        padding = padding,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onLoginClicked = viewModel::onLoginClicked,
        navigateToSignUp = viewModel::navigateToSignUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginScreen(
    state: LoginUiState,
    padding: PaddingValues,
    onEmailChanged: (TextFieldValue) -> Unit,
    onPasswordChanged: (TextFieldValue) -> Unit,
    onLoginClicked: () -> Unit,
    navigateToSignUp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.app_name_english),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = caveatFont
                    ),
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
            ),
        )

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.login_subtitle),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )

            Spacer(Modifier.height(40.dp))

            EmailInputTextField(
                value = state.email,
                onValueChange = onEmailChanged,
                error = state.emailError?.let { stringResource(it) },
            )

            Spacer(Modifier.height(16.dp))

            PasswordInputTextField(
                value = state.password,
                onValueChange = onPasswordChanged,
                error = state.passwordError?.let { stringResource(it) },
            )

            Spacer(Modifier.height(24.dp))

            GlimButton(
                text = if (state.isLoading) {
                    stringResource(R.string.login_loading)
                } else {
                    stringResource(R.string.login_button)
                },
                onClick = onLoginClicked,
                enabled = state.isLoginEnabled && !state.isLoading,
            )

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = navigateToSignUp) {
                Text(stringResource(id = R.string.login_signup), color = Color.Black)
            }

            // 키보드가 올라올 때를 위한 추가 여백
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Preview(name = "Empty Form", showBackground = true)
@Composable
fun PreviewLoginScreen_Empty() {
    LoginScreen(
        state = LoginUiState(),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        navigateToSignUp = {}
    )
}

@Preview(name = "With Errors", showBackground = true)
@Composable
fun PreviewLoginScreen_Errors() {
    LoginScreen(
        state = LoginUiState(
            email = TextFieldValue("invalid-email"),
            password = TextFieldValue("short"),
            emailError = R.string.error_email_invalid,
            passwordError = R.string.error_password_invalid,
        ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        navigateToSignUp = {}
    )
}

@Preview(name = "Valid Input", showBackground = true)
@Composable
fun PreviewLoginScreen_Valid() {
    LoginScreen(
        state = LoginUiState(
            email = TextFieldValue("user@example.com"),
            password = TextFieldValue("Aa1!abcd"),
        ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        navigateToSignUp = {}
    )
}

@Preview(name = "Loading State", showBackground = true)
@Composable
fun PreviewLoginScreen_Loading() {
    LoginScreen(
        state = LoginUiState(
            email = TextFieldValue("user@example.com"),
            password = TextFieldValue("Aa1!abcd"),
            isLoading = true,
        ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        navigateToSignUp = {}
    )
}
