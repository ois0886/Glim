package com.ssafy.glim

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.domain.usecase.auth.LoginUseCase
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.feature.auth.login.LoginSideEffect
import com.ssafy.glim.feature.auth.login.LoginUiState
import com.ssafy.glim.feature.auth.login.LoginViewModel
import com.ssafy.glim.feature.auth.login.component.SocialProvider
import io.mockk.coEvery
import io.mockk.coVerify
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
   19개의 테스트
 */
@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private val mockNavigator = mockk<Navigator>(relaxed = true)
    private val mockLoginUseCase = mockk<LoginUseCase>()
    private val mockLoggedInUseCase = mockk<LoggedInUseCase>()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        mockkObject(ValidationUtils)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkObject(ValidationUtils)
        unmockkStatic(Log::class)
    }

    private fun createViewModel() {
        viewModel = LoginViewModel(
            navigator = mockNavigator,
            loginUseCase = mockLoginUseCase,
            loggedInUseCase = mockLoggedInUseCase
        )
    }

    @Test
    fun `자동_로그인_성공_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } returns Unit

        // When
        createViewModel()

        // Then
        viewModel.test(this) {
            // 자동으로 초기 상태 체크됨
        }

        coVerify { mockLoggedInUseCase() }
    }

    @Test
    fun `자동_로그인_실패_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")

        // When
        createViewModel()

        // Then
        viewModel.test(this) {
            // 자동으로 초기 상태 체크됨
        }

        coVerify { mockLoggedInUseCase() }
    }

    @Test
    fun `이메일_입력_유효한_값_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
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
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
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
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
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
    fun `비밀번호_입력_유효한_값_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        every {
            ValidationUtils.validatePassword(
                password = "validPassword123",
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
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
    fun `비밀번호_입력_빈_값_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        every {
            ValidationUtils.validatePassword(
                password = "",
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
            )
        } returns ValidationResult.Invalid(R.string.error_password_empty)

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onPasswordChanged(TextFieldValue(""))

            awaitState().run {
                assert(password.text == "")
                assert(passwordError == R.string.error_password_empty)
            }
        }
    }

    @Test
    fun `비밀번호_입력_잘못된_형식_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        every {
            ValidationUtils.validatePassword(
                password = "123",
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
            )
        } returns ValidationResult.Invalid(R.string.error_password_invalid)

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
    fun `로그인_성공_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        every {
            ValidationUtils.validateEmail(
                email = "test@example.com",
                emptyErrorRes = R.string.error_email_empty,
                invalidErrorRes = R.string.error_email_invalid
            )
        } returns ValidationResult.Valid
        every {
            ValidationUtils.validatePassword(
                password = "validPassword123",
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
            )
        } returns ValidationResult.Valid
        coEvery { mockLoginUseCase("test@example.com", "validPassword123") } returns Unit

        createViewModel()

        // When & Then
        viewModel.test(
            this,
            LoginUiState(
                email = TextFieldValue("test@example.com"),
                password = TextFieldValue("validPassword123")
            )
        ) {
            containerHost.onLoginClicked()

            // 유효성 검사 결과 확인
            awaitState().run {
                assert(emailError == null) { "Expected emailError to be null, but was $emailError" }
                assert(passwordError == null) { "Expected passwordError to be null, but was $passwordError" }
            }

            // 최종 상태 확인 (로그인 완료 후)
            // 로딩 상태는 너무 빠르게 지나가서 확인하지 않음
            awaitState().run {
                assert(isLoading == false) { "Expected isLoading to be false after login completion, but was $isLoading" }
            }
        }

        coVerify { mockLoginUseCase("test@example.com", "validPassword123") }
    }

    @Test
    fun `로그인_실패_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        every {
            ValidationUtils.validateEmail(
                email = "test@example.com",
                emptyErrorRes = R.string.error_email_empty,
                invalidErrorRes = R.string.error_email_invalid
            )
        } returns ValidationResult.Valid
        every {
            ValidationUtils.validatePassword(
                password = "validPassword123",
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
            )
        } returns ValidationResult.Valid
        coEvery {
            mockLoginUseCase(
                "test@example.com",
                "validPassword123"
            )
        } throws Exception("로그인 실패")

        createViewModel()

        // When & Then
        viewModel.test(
            this,
            LoginUiState(
                email = TextFieldValue("test@example.com"),
                password = TextFieldValue("validPassword123")
            )
        ) {
            containerHost.onLoginClicked()

            // 유효성 검사 통과 상태
            awaitState().run {
                assert(emailError == null) { "Expected emailError to be null, but was $emailError" }
                assert(passwordError == null) { "Expected passwordError to be null, but was $passwordError" }
            }

            // 로그인 실패 후 최종 상태
            awaitState().run {
                assert(isLoading == false) { "Expected isLoading to be false after login failure, but was $isLoading" }
            }

            // 사이드 이펙트 확인
            expectSideEffect(
                LoginSideEffect.ShowError(R.string.login_failed)
            )
        }

        coVerify { mockLoginUseCase("test@example.com", "validPassword123") }
    }

    @Test
    fun `로그인_유효성_검사_실패_이메일_오류_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        every {
            ValidationUtils.validateEmail(
                email = "",
                emptyErrorRes = R.string.error_email_empty,
                invalidErrorRes = R.string.error_email_invalid
            )
        } returns ValidationResult.Invalid(R.string.error_email_empty)
        every {
            ValidationUtils.validatePassword(
                password = "validPassword123",
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
            )
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(
            this,
            LoginUiState(
                email = TextFieldValue(""),
                password = TextFieldValue("validPassword123")
            )
        ) {
            containerHost.onLoginClicked()

            awaitState().run {
                assert(emailError == R.string.error_email_empty)
                assert(passwordError == null)
            }

            expectSideEffect(
                LoginSideEffect.ShowError(R.string.error_email_empty)
            )
        }

        coVerify(exactly = 0) { mockLoginUseCase(any(), any()) }
    }

    @Test
    fun `로그인_유효성_검사_실패_비밀번호_오류_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        every {
            ValidationUtils.validateEmail(
                email = "test@example.com",
                emptyErrorRes = R.string.error_email_empty,
                invalidErrorRes = R.string.error_email_invalid
            )
        } returns ValidationResult.Valid
        every {
            ValidationUtils.validatePassword(
                password = "",
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
            )
        } returns ValidationResult.Invalid(R.string.error_password_empty)

        createViewModel()

        // When & Then
        viewModel.test(
            this,
            LoginUiState(
                email = TextFieldValue("test@example.com"),
                password = TextFieldValue("")
            )
        ) {
            containerHost.onLoginClicked()

            awaitState().run {
                assert(emailError == null)
                assert(passwordError == R.string.error_password_empty)
            }

            expectSideEffect(
                LoginSideEffect.ShowError(R.string.error_password_empty)
            )
        }

        coVerify(exactly = 0) { mockLoginUseCase(any(), any()) }
    }

    @Test
    fun `로그인_유효성_검사_실패_둘_다_오류_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        every {
            ValidationUtils.validateEmail(
                email = "",
                emptyErrorRes = R.string.error_email_empty,
                invalidErrorRes = R.string.error_email_invalid
            )
        } returns ValidationResult.Invalid(R.string.error_email_empty)
        every {
            ValidationUtils.validatePassword(
                password = "",
                emptyErrorRes = R.string.error_password_empty,
                invalidErrorRes = R.string.error_password_invalid
            )
        } returns ValidationResult.Invalid(R.string.error_password_empty)

        createViewModel()

        // When & Then
        viewModel.test(
            this,
            LoginUiState(
                email = TextFieldValue(""),
                password = TextFieldValue("")
            )
        ) {
            containerHost.onLoginClicked()

            awaitState().run {
                assert(emailError == R.string.error_email_empty)
                assert(passwordError == R.string.error_password_empty)
            }

            expectSideEffect(
                LoginSideEffect.ShowError(R.string.error_email_empty)
            )
        }

        coVerify(exactly = 0) { mockLoginUseCase(any(), any()) }
    }

    @Test
    fun `비밀번호_찾기_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.navigateToForgotPassword()

            expectSideEffect(
                LoginSideEffect.ShowError(R.string.not_ready_function)
            )
        }
    }

    @Test
    fun `구글_소셜_로그인_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.navigateToSocialLogin(SocialProvider.GOOGLE)

            expectSideEffect(
                LoginSideEffect.ShowError(R.string.social_login_message)
            )
        }
    }

    @Test
    fun `카카오_소셜_로그인_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.navigateToSocialLogin(SocialProvider.KAKAO)

            expectSideEffect(
                LoginSideEffect.ShowError(R.string.social_login_message)
            )
        }
    }

    @Test
    fun `네이버_소셜_로그인_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.navigateToSocialLogin(SocialProvider.NAVER)

            expectSideEffect(
                LoginSideEffect.ShowError(R.string.social_login_message)
            )
        }
    }

    @Test
    fun `초기_상태_확인_테스트`() = runTest {
        val initialState = LoginUiState()

        assert(initialState.email.text == "")
        assert(initialState.password.text == "")
        assert(initialState.emailError == null)
        assert(initialState.passwordError == null)
        assert(initialState.isLoading == false)
    }

    @Test
    fun `연속_입력_값_변경_테스트`() = runTest {
        // Given
        coEvery { mockLoggedInUseCase() } throws Exception("자동 로그인 실패")
        every {
            ValidationUtils.validateEmail(any(), any(), any())
        } returns ValidationResult.Valid
        every {
            ValidationUtils.validatePassword(any(), any(), any())
        } returns ValidationResult.Valid

        createViewModel()

        // When & Then
        viewModel.test(this) {
            containerHost.onEmailChanged(TextFieldValue("first@example.com"))
            awaitState().run {
                assert(email.text == "first@example.com")
                assert(emailError == null)
            }

            containerHost.onEmailChanged(TextFieldValue("second@example.com"))
            awaitState().run {
                assert(email.text == "second@example.com")
                assert(emailError == null)
            }

            containerHost.onPasswordChanged(TextFieldValue("password1"))
            awaitState().run {
                assert(password.text == "password1")
                assert(passwordError == null)
            }

            containerHost.onPasswordChanged(TextFieldValue("password2"))
            awaitState().run {
                assert(password.text == "password2")
                assert(passwordError == null)
            }
        }
    }
}
