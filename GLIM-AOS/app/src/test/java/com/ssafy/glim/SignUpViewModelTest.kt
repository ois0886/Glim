package com.ssafy.glim

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.domain.usecase.auth.SignUpUseCase
import com.ssafy.glim.core.domain.usecase.auth.VerifyEmailUseCase
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.feature.auth.signup.SignUpStep
import com.ssafy.glim.feature.auth.signup.SignUpUiState
import com.ssafy.glim.feature.auth.signup.SignUpViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.test.test

/*
    21개의 테스트
 */
@ExperimentalCoroutinesApi
class SignUpViewModelTest {

    private val mockNavigator = mockk<Navigator>(relaxed = true)
    private val mockSignUpUseCase = mockk<SignUpUseCase>()
    private val mockVerifyEmailUseCase = mockk<VerifyEmailUseCase>()

    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        mockkObject(ValidationUtils)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkObject(ValidationUtils)
        unmockkStatic(Log::class)
    }

    private fun createViewModel() {
        viewModel = SignUpViewModel(
            navigator = mockNavigator,
            signUpUseCase = mockSignUpUseCase,
            verifyEmailUseCase = mockVerifyEmailUseCase
        )
    }

    @Test
    fun `초기_상태_확인_테스트`() = runTest {
        val initialState = SignUpUiState()

        assert(initialState.email.text == "")
        assert(initialState.code.text == "")
        assert(initialState.password.text == "")
        assert(initialState.confirmPassword.text == "")
        assert(initialState.name.text == "")
        assert(initialState.birthDate == "")
        assert(initialState.gender == null)
        assert(initialState.currentStep == SignUpStep.Email)
        assert(initialState.isLoading == false)
        assert(initialState.actualVerificationCode == "")
        assert(initialState.emailError == null)
        assert(initialState.codeError == null)
        assert(initialState.passwordError == null)
        assert(initialState.confirmPasswordError == null)
        assert(initialState.nameError == null)
        assert(initialState.birthDateError == null)
        assert(initialState.isCurrentStepValid == false)
    }

    @Test
    fun `SignUpStep_진행도_확인_테스트`() = runTest {
        assert(SignUpStep.Email.progress == 0.25f)
        assert(SignUpStep.Code.progress == 0.5f)
        assert(SignUpStep.Password.progress == 0.75f)
        assert(SignUpStep.Profile.progress == 1f)
    }

    @Test
    fun `SignUpStep_다음_단계_확인_테스트`() = runTest {
        assert(SignUpStep.Email.next() == SignUpStep.Code)
        assert(SignUpStep.Code.next() == SignUpStep.Password)
        assert(SignUpStep.Password.next() == SignUpStep.Profile)
        assert(SignUpStep.Profile.next() == null)
    }

    @Test
    fun `SignUpStep_이전_단계_확인_테스트`() = runTest {
        assert(SignUpStep.Email.prev() == null)
        assert(SignUpStep.Code.prev() == SignUpStep.Email)
        assert(SignUpStep.Password.prev() == SignUpStep.Code)
        assert(SignUpStep.Profile.prev() == SignUpStep.Password)
    }

    @Test
    fun `isCurrentStepValid_이메일_단계_테스트`() = runTest {
        // 빈 이메일
        var state = SignUpUiState(currentStep = SignUpStep.Email)
        assert(state.isCurrentStepValid == false)

        // 유효한 이메일, 에러 없음
        state = SignUpUiState(
            currentStep = SignUpStep.Email,
            email = TextFieldValue("test@example.com"),
            emailError = null
        )
        assert(state.isCurrentStepValid == true)

        // 유효한 이메일이지만 에러 있음
        state = SignUpUiState(
            currentStep = SignUpStep.Email,
            email = TextFieldValue("test@example.com"),
            emailError = R.string.error_email_invalid
        )
        assert(state.isCurrentStepValid == false)
    }

    @Test
    fun `isCurrentStepValid_인증코드_단계_테스트`() = runTest {
        // 빈 코드
        var state = SignUpUiState(currentStep = SignUpStep.Code)
        assert(state.isCurrentStepValid == false)

        // 유효한 코드, 에러 없음
        state = SignUpUiState(
            currentStep = SignUpStep.Code,
            code = TextFieldValue("123456"),
            codeError = null
        )
        assert(state.isCurrentStepValid == true)

        // 유효한 코드이지만 에러 있음
        state = SignUpUiState(
            currentStep = SignUpStep.Code,
            code = TextFieldValue("123456"),
            codeError = R.string.error_code_invalid
        )
        assert(state.isCurrentStepValid == false)
    }

    @Test
    fun `isCurrentStepValid_비밀번호_단계_테스트`() = runTest {
        // 모든 필드 빈 값
        var state = SignUpUiState(currentStep = SignUpStep.Password)
        assert(state.isCurrentStepValid == false)

        // 비밀번호만 있음
        state = SignUpUiState(
            currentStep = SignUpStep.Password,
            password = TextFieldValue("password123")
        )
        assert(state.isCurrentStepValid == false)

        // 비밀번호 확인만 있음
        state = SignUpUiState(
            currentStep = SignUpStep.Password,
            confirmPassword = TextFieldValue("password123")
        )
        assert(state.isCurrentStepValid == false)

        // 모든 필드 있고 에러 없음
        state = SignUpUiState(
            currentStep = SignUpStep.Password,
            password = TextFieldValue("password123"),
            confirmPassword = TextFieldValue("password123"),
            passwordError = null,
            confirmPasswordError = null
        )
        assert(state.isCurrentStepValid == true)

        // 모든 필드 있지만 에러 있음
        state = SignUpUiState(
            currentStep = SignUpStep.Password,
            password = TextFieldValue("password123"),
            confirmPassword = TextFieldValue("differentPassword"),
            passwordError = null,
            confirmPasswordError = R.string.error_password_mismatch
        )
        assert(state.isCurrentStepValid == false)
    }

    @Test
    fun `isCurrentStepValid_프로필_단계_테스트`() = runTest {
        // 모든 필드 빈 값
        var state = SignUpUiState(currentStep = SignUpStep.Profile)
        assert(state.isCurrentStepValid == false)

        // 이름만 있음
        state = SignUpUiState(
            currentStep = SignUpStep.Profile,
            name = TextFieldValue("홍길동")
        )
        assert(state.isCurrentStepValid == false)

        // 생년월일만 있음
        state = SignUpUiState(
            currentStep = SignUpStep.Profile,
            birthDate = "19900101"
        )
        assert(state.isCurrentStepValid == false)

        // 성별만 있음
        state = SignUpUiState(
            currentStep = SignUpStep.Profile,
            gender = "M"
        )
        assert(state.isCurrentStepValid == false)

        // 모든 필드 있고 에러 없음
        state = SignUpUiState(
            currentStep = SignUpStep.Profile,
            name = TextFieldValue("홍길동"),
            birthDate = "19900101",
            gender = "M",
            nameError = null,
            birthDateError = null
        )
        assert(state.isCurrentStepValid == true)

        // 모든 필드 있지만 에러 있음
        state = SignUpUiState(
            currentStep = SignUpStep.Profile,
            name = TextFieldValue("홍길동"),
            birthDate = "19900101",
            gender = "M",
            nameError = R.string.error_name_invalid,
            birthDateError = null
        )
        assert(state.isCurrentStepValid == false)
    }

    @Test
    fun `이메일_입력_유효한_값_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validateEmail(
                email = "test@example.com",
                emptyErrorRes = R.string.error_email_empty,
                invalidErrorRes = R.string.error_email_invalid
            )
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onEmailChanged(TextFieldValue("test@example.com"))

            awaitState().run {
                assert(email.text == "test@example.com")
                assert(emailError == null)
            }
        }
    }

    @Test
    fun `이메일_입력_빈_값_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validateEmail(
                email = "",
                emptyErrorRes = R.string.error_email_empty,
                invalidErrorRes = R.string.error_email_invalid
            )
        } returns ValidationResult.Invalid(R.string.error_email_empty)

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onEmailChanged(TextFieldValue(""))

            awaitState().run {
                assert(email.text == "")
                assert(emailError == R.string.error_email_empty)
            }
        }
    }

    @Test
    fun `이메일_입력_잘못된_형식_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validateEmail(
                email = "invalid-email",
                emptyErrorRes = R.string.error_email_empty,
                invalidErrorRes = R.string.error_email_invalid
            )
        } returns ValidationResult.Invalid(R.string.error_email_invalid)

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onEmailChanged(TextFieldValue("invalid-email"))

            awaitState().run {
                assert(email.text == "invalid-email")
                assert(emailError == R.string.error_email_invalid)
            }
        }
    }

    @Test
    fun `인증코드_입력_유효한_값_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validateCode(
                code = "123456",
                emptyErrorRes = R.string.error_code_empty,
                invalidErrorRes = R.string.error_code_invalid
            )
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onCodeChanged(TextFieldValue("123456"))

            awaitState().run {
                assert(code.text == "123456")
                assert(codeError == null)
            }
        }
    }

    @Test
    fun `인증코드_입력_숫자_추출_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validateCode(
                code = "123456",
                emptyErrorRes = R.string.error_code_empty,
                invalidErrorRes = R.string.error_code_invalid
            )
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onCodeChanged(TextFieldValue("12a34b56"))

            awaitState().run {
                assert(code.text == "123456") // 숫자만 추출됨
                assert(codeError == null)
            }
        }
    }

    @Test
    fun `비밀번호_입력_유효한_값_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validatePassword(
                password = "validPassword123",
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
            )
        } returns ValidationResult.Valid
        every {
            ValidationUtils.validatePasswordConfirm(
                password = "validPassword123",
                confirmPassword = "",
                mismatchErrorRes = R.string.error_password_mismatch
            )
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onPasswordChanged(TextFieldValue("validPassword123"))

            awaitState().run {
                assert(password.text == "validPassword123")
                assert(passwordError == null)
            }
        }
    }

    @Test
    fun `비밀번호_입력_잘못된_형식_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validatePassword(
                password = "123",
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
            )
        } returns ValidationResult.Invalid(R.string.error_password_invalid)
        every {
            ValidationUtils.validatePasswordConfirm(any(), any(), any())
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onPasswordChanged(TextFieldValue("123"))

            awaitState().run {
                assert(password.text == "123")
                assert(passwordError == R.string.error_password_invalid)
            }
        }
    }

    @Test
    fun `비밀번호_확인_일치_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validatePasswordConfirm(
                password = "validPassword123",
                confirmPassword = "validPassword123",
                mismatchErrorRes = R.string.error_password_mismatch
            )
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(
            this,
            SignUpUiState(password = TextFieldValue("validPassword123"))
        ) {
            containerHost.onConfirmPasswordChanged(TextFieldValue("validPassword123"))

            awaitState().run {
                assert(confirmPassword.text == "validPassword123")
                assert(confirmPasswordError == null)
            }
        }
    }

    @Test
    fun `비밀번호_확인_불일치_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validatePasswordConfirm(
                password = "validPassword123",
                confirmPassword = "differentPassword",
                mismatchErrorRes = R.string.error_password_mismatch
            )
        } returns ValidationResult.Invalid(R.string.error_password_mismatch)

        createViewModel()

        // When & Then
        viewModel.test(
            this,
            SignUpUiState(password = TextFieldValue("validPassword123"))
        ) {
            containerHost.onConfirmPasswordChanged(TextFieldValue("differentPassword"))

            awaitState().run {
                assert(confirmPassword.text == "differentPassword")
                assert(confirmPasswordError == R.string.error_password_mismatch)
            }
        }
    }

    @Test
    fun `이름_입력_유효한_값_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validateName(
                name = "홍길동",
                emptyErrorRes = R.string.error_name_empty,
                invalidErrorRes = R.string.error_name_invalid
            )
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onNameChanged(TextFieldValue("홍길동"))

            awaitState().run {
                assert(name.text == "홍길동")
                assert(nameError == null)
            }
        }
    }

    @Test
    fun `이름_입력_잘못된_형식_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validateName(
                name = "123",
                emptyErrorRes = R.string.error_name_empty,
                invalidErrorRes = R.string.error_name_invalid
            )
        } returns ValidationResult.Invalid(R.string.error_name_invalid)

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onNameChanged(TextFieldValue("123"))

            awaitState().run {
                assert(name.text == "123")
                assert(nameError == R.string.error_name_invalid)
            }
        }
    }

    @Test
    fun `생년월일_입력_유효한_값_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validateBirthDate(
                birthDate = "19900101",
                emptyErrorRes = R.string.error_birth_empty,
                formatErrorRes = R.string.error_birth_format,
                yearErrorRes = R.string.error_birth_year,
                monthErrorRes = R.string.error_birth_month,
                dayErrorRes = R.string.error_birth_day,
                futureDateErrorRes = R.string.error_birth_future
            )
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onBirthChanged("19900101")

            awaitState().run {
                assert(birthDate == "19900101")
                assert(birthDateError == null)
            }
        }
    }

    @Test
    fun `생년월일_입력_숫자_추출_테스트`() = runTest {
        // Given
        every {
            ValidationUtils.validateBirthDate(
                birthDate = "19900101",
                emptyErrorRes = R.string.error_birth_empty,
                formatErrorRes = R.string.error_birth_format,
                yearErrorRes = R.string.error_birth_year,
                monthErrorRes = R.string.error_birth_month,
                dayErrorRes = R.string.error_birth_day,
                futureDateErrorRes = R.string.error_birth_future
            )
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onBirthChanged("1990-01-01")

            awaitState().run {
                assert(birthDate == "19900101")
                assert(birthDateError == null)
            }
        }
    }
}
