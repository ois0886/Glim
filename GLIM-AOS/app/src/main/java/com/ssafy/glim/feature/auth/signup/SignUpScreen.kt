package com.ssafy.glim.feature.auth.signup

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.ui.GlimTopBar
import com.ssafy.glim.core.ui.TitleAlignment
import com.ssafy.glim.feature.auth.login.component.GlimButton
import com.ssafy.glim.feature.auth.signup.component.EmailAuthInputContent
import com.ssafy.glim.feature.auth.signup.component.EmailVerificationCodeInputContent
import com.ssafy.glim.feature.auth.signup.component.PasswordConfirmInputContent
import com.ssafy.glim.feature.auth.signup.component.ProgressIndicatorBar
import com.ssafy.glim.feature.auth.signup.component.TermsConsentContent
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
        onBirthChanged = viewModel::onBirthChanged,
        onGenderSelected = viewModel::onGenderSelected,
        onNextStep = { viewModel.onNextStep(context) },
        onBackStep = viewModel::onBackStep,
        onToggleAll = viewModel::onToggleAll,
        onToggleTerms = viewModel::onToggleTerms,
        onTogglePrivacy = viewModel::onTogglePrivacy,
        onToggleMarketing = viewModel::onToggleMarketing,
        onOpenTerms = { viewModel.onOpenTerms(context) },
        onOpenPrivacy = { viewModel.onOpenPrivacy(context) },
    )
}

@Composable
private fun SignUpScreen(
    state: SignUpUiState,
    padding: PaddingValues,
    onEmailChanged: (TextFieldValue) -> Unit,
    onCodeChanged: (TextFieldValue) -> Unit,
    onPasswordChanged: (TextFieldValue) -> Unit,
    onConfirmPasswordChanged: (TextFieldValue) -> Unit,
    onNameChanged: (TextFieldValue) -> Unit,
    onBirthChanged: (TextFieldValue) -> Unit,
    onGenderSelected: (String) -> Unit,
    onNextStep: () -> Unit,
    onBackStep: () -> Unit,
    onToggleAll: (Boolean) -> Unit = {},
    onToggleTerms: (Boolean) -> Unit = {},
    onTogglePrivacy: (Boolean) -> Unit = {},
    onToggleMarketing: (Boolean) -> Unit = {},
    onOpenTerms: () -> Unit = {},
    onOpenPrivacy: () -> Unit = {}
) {
    BackHandler(enabled = state.currentStep != SignUpStep.Email) {
        onBackStep()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        GlimTopBar(
            title = stringResource(R.string.login_signup),
            showBack = state.currentStep != SignUpStep.Email,
            onBack = onBackStep,
            alignment = TitleAlignment.Center
        )

        ProgressIndicatorBar(progress = state.currentStep.progress)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state.currentStep) {
                SignUpStep.Terms ->
                    TermsConsentContent(
                        termsChecked = state.termsAgree,
                        privacyChecked = state.privacyAgree,
                        marketingChecked = state.marketingAgree,
                        allChecked = state.allAgree,
                        onToggleTerms = onToggleTerms,
                        onTogglePrivacy = onTogglePrivacy,
                        onToggleMarketing = onToggleMarketing,
                        onToggleAll = onToggleAll,
                        onOpenTerms = onOpenTerms,
                        onOpenPrivacy = onOpenPrivacy
                    )

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
                        isUpdate = false,
                        name = state.name,
                        onNameChange = onNameChanged,
                        nameError = state.nameError?.let { stringResource(it) },
                        birthYear = state.birthDate,
                        onBirthYearChange = onBirthChanged,
                        birthYearError = state.birthDateError?.let { stringResource(it) },
                        selectedGender = state.gender,
                        onGenderSelect = onGenderSelected,
                    )
            }

            Spacer(modifier = Modifier.weight(1f))

            GlimButton(
                text = when (state.currentStep) {
                    SignUpStep.Terms -> "동의하고 시작하기"
                    SignUpStep.Email -> stringResource(R.string.signup_send_verification_code)
                    SignUpStep.Code -> stringResource(R.string.signup_verify_code)
                    SignUpStep.Password -> stringResource(R.string.password_confirm)
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
            email = TextFieldValue("user@example.com"),
            emailError = null,
        ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onCodeChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onNameChanged = {},
        onBirthChanged = {},
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
            email = TextFieldValue("invalid-email"),
            emailError = R.string.error_email_invalid,
        ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onCodeChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onNameChanged = {},
        onBirthChanged = {},
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
            code = TextFieldValue("123456"),
            codeError = null,
        ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onCodeChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onNameChanged = {},
        onBirthChanged = {},
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
            password = TextFieldValue("Aa1!aaaa"),
            confirmPassword = TextFieldValue("Aa1!aaaa"),
            passwordError = null,
            confirmPasswordError = null,
        ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onCodeChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onNameChanged = {},
        onBirthChanged = {},
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
            password = TextFieldValue("short"),
            confirmPassword = TextFieldValue("different"),
            passwordError = R.string.error_password_invalid,
            confirmPasswordError = R.string.error_password_mismatch,
        ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onCodeChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onNameChanged = {},
        onBirthChanged = {},
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
            name = TextFieldValue("인성"),
            birthDate = TextFieldValue("1998"),
            gender = "남성",
            nameError = null,
            birthDateError = null,
        ),
        padding = PaddingValues(0.dp),
        onEmailChanged = {},
        onCodeChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onNameChanged = {},
        onBirthChanged = {},
        onGenderSelected = {},
        onNextStep = {},
        onBackStep = {},
    )
}
