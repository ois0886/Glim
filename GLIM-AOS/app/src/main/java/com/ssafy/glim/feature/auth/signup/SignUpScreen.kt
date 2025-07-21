package com.ssafy.glim.feature.auth.signup

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.ui.GlimTopBar
import com.ssafy.glim.core.ui.TitleAlignment
import com.ssafy.glim.feature.auth.login.component.GlimButton
import com.ssafy.glim.feature.auth.signup.component.EmailAuthInputContent
import com.ssafy.glim.feature.auth.signup.component.EmailVerificationCodeInputContent
import com.ssafy.glim.feature.auth.signup.component.PasswordConfirmInputContent
import com.ssafy.glim.feature.auth.signup.component.ProgressIndicatorBar
import com.ssafy.glim.feature.auth.signup.component.UserProfileInputContent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun SignUpRoute(
    padding: PaddingValues,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value
    val context = LocalContext.current

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is SignUpSideEffect.ShowToast ->
                Toast.makeText(context, context.getString(effect.message), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    SignUpScreen(
        state = state,
        padding = padding,
        onEmailChanged = viewModel::onEmailChanged,
        onCodeChanged = viewModel::onCodeChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onNameChanged = viewModel::onNameChanged,
        onBirthYearChanged = viewModel::onBirthChanged,
        onGenderSelected = viewModel::onGenderSelected,
        onNextStep = viewModel::onNextStep,
        onBackStep = viewModel::onBackStep,
    )
}

@Composable
private fun SignUpScreen(
    state: SignUpUiState,
    padding: PaddingValues,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state.currentStep) {
                SignUpStep.Email ->
                    EmailAuthInputContent(
                        value = state.email,
                        onValueChange = onEmailChanged,
                        error = state.emailError?.let { stringResource(it) },
                    )

                SignUpStep.Code ->
                    EmailVerificationCodeInputContent(
                        value = state.code,
                        onValueChange = onCodeChanged,
                        error = state.codeError?.let { stringResource(it) },
                    )

                SignUpStep.Password ->
                    PasswordConfirmInputContent(
                        password = state.password,
                        onPasswordChange = onPasswordChanged,
                        confirmPassword = state.confirmPassword,
                        onConfirmPasswordChange = onConfirmPasswordChanged,
                        passwordError = state.passwordError?.let { stringResource(it) },
                        confirmPasswordError = state.confirmPasswordError?.let { stringResource(it) },
                    )

                SignUpStep.Profile ->
                    UserProfileInputContent(
                        name = state.name,
                        onNameChange = onNameChanged,
                        nameError = state.nameError?.let { stringResource(it) },
                        birthYear = state.birthYear,
                        onBirthYearChange = onBirthYearChanged,
                        birthYearError = state.birthYearError?.let { stringResource(it) },
                        selectedGender = state.gender,
                        onGenderSelect = onGenderSelected,
                    )
            }

            Spacer(modifier = Modifier.weight(1f))

            GlimButton(
                text = when (state.currentStep) {
                    SignUpStep.Email -> "인증번호 전송"
                    SignUpStep.Code -> "인증완료"
                    SignUpStep.Password -> "확인"
                    SignUpStep.Profile -> stringResource(R.string.login_signup)
                },
                onClick = onNextStep,
                enabled = state.isCurrentStepValid && !state.isLoading,
            )
        }
    }
}

@Preview(name = "Step 1 - Email Input", showBackground = true)
@Composable
private fun PreviewSignUpScreen_EmailStep() {
    SignUpScreen(
        state = SignUpUiState(
            currentStep = SignUpStep.Email,
            email = "user@example.com",
            emailError = null,
        ),
        padding = PaddingValues(0.dp),
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

@Preview(name = "Step 1 - Email Input with Error", showBackground = true)
@Composable
private fun PreviewSignUpScreen_EmailStepWithError() {
    SignUpScreen(
        state = SignUpUiState(
            currentStep = SignUpStep.Email,
            email = "invalid-email",
            emailError = R.string.error_email_invalid,
        ),
        padding = PaddingValues(0.dp),
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
        state = SignUpUiState(
            currentStep = SignUpStep.Code,
            code = "123456",
            codeError = null,
        ),
        padding = PaddingValues(0.dp),
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
        state = SignUpUiState(
            currentStep = SignUpStep.Password,
            password = "Aa1!aaaa",
            confirmPassword = "Aa1!aaaa",
            passwordError = null,
            confirmPasswordError = null,
        ),
        padding = PaddingValues(0.dp),
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

@Preview(name = "Step 3 - Password Input with Error", showBackground = true)
@Composable
private fun PreviewSignUpScreen_PasswordStepWithError() {
    SignUpScreen(
        state = SignUpUiState(
            currentStep = SignUpStep.Password,
            password = "short",
            confirmPassword = "different",
            passwordError = R.string.error_password_invalid,
            confirmPasswordError = R.string.error_password_mismatch,
        ),
        padding = PaddingValues(0.dp),
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
        state = SignUpUiState(
            currentStep = SignUpStep.Profile,
            name = "인성",
            birthYear = "1998",
            gender = "남성",
            nameError = null,
            birthYearError = null,
        ),
        padding = PaddingValues(0.dp),
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