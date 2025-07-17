package com.example.myapplication.feature.auth.signup

import android.content.Context
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import com.example.myapplication.R
import com.example.myapplication.core.domain.usecase.auth.CertifyValidCodeUseCase
import com.example.myapplication.core.domain.usecase.auth.SignUpUseCase
import com.example.myapplication.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
internal class SignUpViewModel
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val navigator: Navigator,
    private val signUpUseCase: SignUpUseCase,
    private val certifyValidCodeUseCase: CertifyValidCodeUseCase,
) : ViewModel(), ContainerHost<SignUpUiState, SignUpSideEffect> {
    companion object {
        private val PASSWORD_REGEX =
            Regex(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,16}$",
            )
        private val CODE_REGEX = Regex("^[0-9]{6}$")
        private val BIRTH_YEAR_REGEX = Regex("^[0-9]{4}$")
        private val NAME_REGEX = Regex("^.{2,16}$")
    }

    override val container = container<SignUpUiState, SignUpSideEffect>(SignUpUiState())

    fun onEmailChanged(email: String) =
        intent {
            val error = if (email.isNotBlank()) validateEmail(email) else null
            reduce { state.copy(email = email, emailError = error) }
        }

    fun onCodeChanged(code: String) =
        intent {
            val filteredCode = code.filter { it.isDigit() }.take(6)
            val error = if (filteredCode.isNotBlank()) validateCode(filteredCode) else null
            reduce { state.copy(code = filteredCode, codeError = error) }
        }

    fun onPasswordChanged(password: String) =
        intent {
            val error = if (password.isNotBlank()) validatePassword(password) else null
            val confirmError =
                if (state.confirmPassword.isNotBlank() && password != state.confirmPassword) {
                    context.getString(R.string.error_password_mismatch)
                } else {
                    null
                }
            reduce {
                state.copy(
                    password = password,
                    passwordError = error,
                    confirmPasswordError = confirmError,
                )
            }
        }

    fun onConfirmPasswordChanged(confirmPassword: String) =
        intent {
            val error =
                if (confirmPassword.isNotBlank() && confirmPassword != state.password) {
                    context.getString(R.string.error_password_mismatch)
                } else {
                    null
                }
            reduce { state.copy(confirmPassword = confirmPassword, confirmPasswordError = error) }
        }

    fun onNameChanged(name: String) =
        intent {
            val error = if (name.isNotBlank()) validateName(name) else null
            reduce { state.copy(name = name, nameError = error) }
        }

    fun onBirthYearChanged(birthYear: String) =
        intent {
            val filteredYear = birthYear.filter { it.isDigit() }.take(4)
            val error = if (filteredYear.isNotBlank()) validateBirthYear(filteredYear) else null
            reduce { state.copy(birthYear = filteredYear, birthYearError = error) }
        }

    fun onGenderSelected(gender: String) =
        intent {
            reduce { state.copy(gender = gender) }
        }

    fun onNextStep() =
        intent {
            when (state.currentStep) {
                SignUpStep.Email -> {
                    val error = validateEmail(state.email)
                    if (error != null) {
                        postSideEffect(SignUpSideEffect.ShowToast(error))
                        reduce { state.copy(emailError = error) }
                        return@intent
                    }
                    moveToNextStep()
                }

                SignUpStep.Code -> {
                    val error = validateCode(state.code)
                    if (error != null) {
                        postSideEffect(SignUpSideEffect.ShowToast(error))
                        reduce { state.copy(codeError = error) }
                        return@intent
                    }
                    certifyValidCode()
                }

                SignUpStep.Password -> {
                    val passwordError = validatePassword(state.password)
                    val confirmError =
                        if (state.password != state.confirmPassword) {
                            context.getString(R.string.error_password_mismatch)
                        } else {
                            null
                        }

                    if (passwordError != null || confirmError != null) {
                        val errorMessage = passwordError ?: confirmError!!
                        postSideEffect(SignUpSideEffect.ShowToast(errorMessage))
                        reduce {
                            state.copy(
                                passwordError = passwordError,
                                confirmPasswordError = confirmError,
                            )
                        }
                        return@intent
                    }
                    moveToNextStep()
                }

                SignUpStep.Profile -> {
                    val nameError = validateName(state.name)
                    val birthYearError = validateBirthYear(state.birthYear)
                    val genderError =
                        if (state.gender == null) {
                            context.getString(R.string.error_gender_empty)
                        } else {
                            null
                        }

                    if (nameError != null || birthYearError != null || genderError != null) {
                        val errorMessage = nameError ?: birthYearError ?: genderError!!
                        postSideEffect(SignUpSideEffect.ShowToast(errorMessage))
                        reduce {
                            state.copy(
                                nameError = nameError,
                                birthYearError = birthYearError,
                            )
                        }
                        return@intent
                    }
                    return@intent
                }
            }
        }

    fun onBackStep() =
        intent {
            state.currentStep.prev()?.let { prev ->
                reduce { state.copy(currentStep = prev) }
            } ?: navigator.navigateBack()
        }

    private fun certifyValidCode() =
        intent {
            certifyValidCodeUseCase(state.code)
                .onStart {
                    reduce { state.copy(isLoading = true) }
                }
                .catch { exception ->
                    reduce { state.copy(isLoading = false) }
                    val errorMessage =
                        exception.message
                            ?: context.getString(R.string.error_code_verification_failed)
                    postSideEffect(SignUpSideEffect.ShowToast(errorMessage))
                    reduce { state.copy(codeError = errorMessage) }
                }
                .collect { result ->
                    reduce { state.copy(isLoading = false) }
                    if (result.isSuccess) {
                        moveToNextStep()
                    } else {
                        val errorMessage = context.getString(R.string.error_code_incorrect)
                        postSideEffect(SignUpSideEffect.ShowToast(errorMessage))
                        reduce { state.copy(codeError = errorMessage) }
                    }
                }
        }

    private fun moveToNextStep() =
        intent {
            state.currentStep.next()?.let { next ->
                reduce { state.copy(currentStep = next) }
            }
        }

    private fun validateEmail(email: String): String? =
        when {
            email.isBlank() -> context.getString(R.string.error_email_empty)
            !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() ->
                context.getString(R.string.error_email_invalid)

            else -> null
        }

    private fun validateCode(code: String): String? =
        when {
            code.isBlank() -> context.getString(R.string.error_code_empty)
            !CODE_REGEX.matches(code) -> context.getString(R.string.error_code_invalid)
            else -> null
        }

    private fun validatePassword(password: String): String? =
        when {
            password.isBlank() -> context.getString(R.string.error_password_empty)
            !PASSWORD_REGEX.matches(password) -> context.getString(R.string.error_password_invalid)
            else -> null
        }

    private fun validateName(name: String): String? =
        when {
            name.isBlank() -> context.getString(R.string.error_name_empty)
            !NAME_REGEX.matches(name) -> context.getString(R.string.error_name_invalid)
            else -> null
        }

    private fun validateBirthYear(birthYear: String): String? =
        when {
            birthYear.isBlank() -> context.getString(R.string.error_birth_year_empty)
            !BIRTH_YEAR_REGEX.matches(birthYear) -> context.getString(R.string.error_birth_year_invalid)
            birthYear.toIntOrNull()?.let { year ->
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                year !in 1900..currentYear
            } == true -> context.getString(R.string.error_birth_year_range)

            else -> null
        }
}
