package com.ssafy.glim.feature.auth.signup

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.ui.GlimTopBar
import com.ssafy.glim.core.ui.TitleAlignment
import com.ssafy.glim.feature.auth.login.component.GlimButton
import com.ssafy.glim.feature.auth.signup.component.AuthInputContent
import com.ssafy.glim.feature.auth.signup.component.EmailVerificationCodeInputContent
import com.ssafy.glim.feature.auth.signup.component.ProgressIndicatorBar
import com.ssafy.glim.feature.auth.signup.component.UserProfileInputContent
import com.ssafy.glim.feature.main.excludeSystemBars
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
    BackHandler(enabled = state.currentStep != SignUpStep.Auth) {
        onBackStep()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding.excludeSystemBars())
            .imePadding()
    ) {
        GlimTopBar(
            title = stringResource(R.string.login_signup),
            showBack = state.currentStep != SignUpStep.Auth,
            onBack = onBackStep,
            alignment = TitleAlignment.Center,
            titleColor = Color.Black,
            titleSize = 20.sp,
        )

        ProgressIndicatorBar(progress = state.currentStep.progress)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state.currentStep) {
                SignUpStep.Auth ->
                    AuthInputContent(
                        email = state.email,
                        onEmailChange = onEmailChanged,
                        emailError = state.emailError?.let { stringResource(it) },
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
                        birthYear = state.birthDate,
                        onBirthYearChange = onBirthYearChanged,
                        birthYearError = state.birthDateError?.let { stringResource(it) },
                        selectedGender = state.gender,
                        onGenderSelect = onGenderSelected,
                    )

                SignUpStep.Code ->
                    EmailVerificationCodeInputContent(
                        value = state.code,
                        onValueChange = onCodeChanged,
                        error = state.codeError?.let { stringResource(it) },
                    )
            }

            Spacer(modifier = Modifier.weight(1f))

            GlimButton(
                text = when (state.currentStep) {
                    SignUpStep.Auth -> stringResource(R.string.signup_next)
                    SignUpStep.Profile -> stringResource(R.string.signup_next)
                    SignUpStep.Code -> stringResource(R.string.signup_verify_code)
                },
                onClick = onNextStep,
                enabled = state.isCurrentStepValid && !state.isLoading,
            )
        }
    }
}