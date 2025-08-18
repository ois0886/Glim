package com.example.myapplication.feature.auth.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.core.ui.GlimTopBar
import com.example.myapplication.core.ui.TitleAlignment
import com.example.myapplication.feature.auth.login.component.GlimButton
import com.example.myapplication.feature.auth.signup.component.EmailAuthInputContent
import com.example.myapplication.feature.auth.signup.component.EmailVerificationCodeInputContent
import com.example.myapplication.feature.auth.signup.component.PasswordConfirmInputContent
import com.example.myapplication.feature.auth.signup.component.ProgressIndicatorBar
import com.example.myapplication.feature.auth.signup.component.UserProfileInputContent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun SignUpRoute(viewModel: SignUpViewModel = hiltViewModel()) {
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SignUpSideEffect.NavigateToMain -> {
                // onNavigateMain()
            }

            is SignUpSideEffect.ShowToast ->
                // TODO: Toast(effect.message)
                Unit
        }
    }

    SignUpScreen(
        state = state,
        onEmailChanged = viewModel::onEmailChanged,
        onCodeChanged = viewModel::onCodeChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onNameChanged = viewModel::onNameChanged,
        onBirthYearChanged = viewModel::onBirthYearChanged,
        onGenderSelected = viewModel::onGenderSelected,
        onNextStep = viewModel::onNextStep,
        onBackStep = viewModel::onBackStep,
    )
}

@Composable
private fun SignUpScreen(
    state: SignUpUiState,
    onEmailChanged: (String) -> Unit,
    onCodeChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onBirthYearChanged: (String) -> Unit,
    onGenderSelected: (String) -> Unit,
    onNextStep: () -> Unit,
    onBackStep: () -> Unit,
) {
    BackHandler(enabled = state.currentStep != SignUpStep.Email) {
        onBackStep()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GlimTopBar(
            title = stringResource(R.string.login_signup),
            showBack = state.currentStep != SignUpStep.Email,
            onBack = onBackStep,
            alignment = TitleAlignment.Center,
            titleColor = Color.Black,
            titleSize = 20.sp,
        )

        ProgressIndicatorBar(progress = state.currentStep.progress)

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state.currentStep) {
                SignUpStep.Email ->
                    EmailAuthInputContent(
                        value = state.email,
                        onValueChange = onEmailChanged,
                        error = state.emailError,
                    )

                SignUpStep.Code ->
                    EmailVerificationCodeInputContent(
                        value = state.code,
                        onValueChange = onCodeChanged,
                        error = state.codeError,
                    )

                SignUpStep.Password ->
                    PasswordConfirmInputContent(
                        password = state.password,
                        onPasswordChange = onPasswordChanged,
                        confirmPassword = state.confirmPassword,
                        onConfirmPasswordChange = onConfirmPasswordChanged,
                        passwordError = state.passwordError,
                        confirmPasswordError = state.confirmPasswordError,
                    )

                SignUpStep.Profile ->
                    UserProfileInputContent(
                        name = state.name,
                        onNameChange = onNameChanged,
                        birthYear = state.birthYear,
                        onBirthYearChange = onBirthYearChanged,
                        selectedGender = state.gender,
                        onGenderSelect = onGenderSelected,
                    )
            }

            Spacer(modifier = Modifier.weight(1f))

            GlimButton(
                text = stringResource(R.string.login_signup),
                onClick = onNextStep,
                enabled = state.isStepValid && !state.isLoading,
            )
        }
    }
}

@Preview(name = "Step 1 - Email Input", showBackground = true)
@Composable
private fun PreviewSignUpScreen_EmailStep() {
    SignUpScreen(
        state =
            SignUpUiState(
                currentStep = SignUpStep.Email,
                email = "user@example.com",
                emailError = null,
            ),
        onEmailChanged = {},
        onCodeChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onNameChanged = {},
        onBirthYearChanged = {},
        onGenderSelected = {},
        onNextStep = {},
        onBackStep = {},
    )
}

@Preview(name = "Step 2 - Code Input", showBackground = true)
@Composable
private fun PreviewSignUpScreen_CodeStep() {
    SignUpScreen(
        state =
            SignUpUiState(
                currentStep = SignUpStep.Code,
                code = "123456",
                codeError = null,
            ),
        onEmailChanged = {},
        onCodeChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onNameChanged = {},
        onBirthYearChanged = {},
        onGenderSelected = {},
        onNextStep = {},
        onBackStep = {},
    )
}

@Preview(name = "Step 3 - Password Input", showBackground = true)
@Composable
private fun PreviewSignUpScreen_PasswordStep() {
    SignUpScreen(
        state =
            SignUpUiState(
                currentStep = SignUpStep.Password,
                password = "Aa1!aaaa",
                confirmPassword = "Aa1!aaaa",
                passwordError = null,
                confirmPasswordError = null,
            ),
        onEmailChanged = {},
        onCodeChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onNameChanged = {},
        onBirthYearChanged = {},
        onGenderSelected = {},
        onNextStep = {},
        onBackStep = {},
    )
}

@Preview(name = "Step 4 - Profile Input", showBackground = true)
@Composable
private fun PreviewSignUpScreen_ProfileStep() {
    SignUpScreen(
        state =
            SignUpUiState(
                currentStep = SignUpStep.Profile,
                name = "인성",
                birthYear = "1998",
                gender = "남성",
            ),
        onEmailChanged = {},
        onCodeChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onNameChanged = {},
        onBirthYearChanged = {},
        onGenderSelected = {},
        onNextStep = {},
        onBackStep = {},
    )
}
