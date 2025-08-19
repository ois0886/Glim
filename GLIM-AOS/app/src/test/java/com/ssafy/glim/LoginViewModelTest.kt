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

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private val mockNavigator = mockk<Navigator>(relaxed = true)
    private val mockLoginUseCase = mockk<LoginUseCase>()

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
            loginUseCase = mockLoginUseCase
        )
    }

    @Test
    fun `이메일_입력_유효`() = runTest {
        every { ValidationUtils.validateEmail(any<String>(), any<Int>(), any<Int>()) } returns ValidationResult.Valid
        createViewModel()
        viewModel.test(this) {
            viewModel.onEmailChanged(TextFieldValue("user@email.com"))
            awaitState().run {
                assert(email.text == "user@email.com")
                assert(emailError == null)
            }
        }
    }

    @Test
    fun `이메일_형식_에러`() = runTest {
        every { ValidationUtils.validateEmail(eq("xx"), any<Int>(), any<Int>()) } returns ValidationResult.Invalid(R.string.error_email_invalid)
        createViewModel()
        viewModel.test(this) {
            viewModel.onEmailChanged(TextFieldValue("xx"))
            awaitState().run { assert(emailError == R.string.error_email_invalid) }
        }
    }

    @Test
    fun `비밀번호_입력_유효`() = runTest {
        every { ValidationUtils.validatePassword(any<String>(), any<Int>(), any<Int>()) } returns ValidationResult.Valid
        createViewModel()
        viewModel.test(this) {
            viewModel.onPasswordChanged(TextFieldValue("pw123456!"))
            awaitState().run { assert(passwordError == null) }
        }
    }

    @Test
    fun `비밀번호_형식_에러`() = runTest {
        every { ValidationUtils.validatePassword(eq("a"), any<Int>(), any<Int>()) } returns ValidationResult.Invalid(R.string.error_password_invalid)
        createViewModel()
        viewModel.test(this) {
            viewModel.onPasswordChanged(TextFieldValue("a"))
            awaitState().run { assert(passwordError == R.string.error_password_invalid) }
        }
    }

    @Test
    fun `로그인 성공`() = runTest {
        every { ValidationUtils.validateEmail(any<String>(), any(), any()) } returns ValidationResult.Valid
        every { ValidationUtils.validatePassword(any<String>(), any(), any()) } returns ValidationResult.Valid
        coEvery { mockLoginUseCase("user@email.com", "pw123!") } returns Unit

        createViewModel()
        viewModel.test(
            this,
            initialState = LoginUiState(
                email = TextFieldValue("user@email.com"),
                password = TextFieldValue("pw123!")
            )
        ) {
            viewModel.onLoginClicked()
            awaitState().run { assert(isLoading) }
            awaitState().run { assert(!isLoading) }
        }
        coVerify { mockLoginUseCase("user@email.com", "pw123!") }
    }

    @Test
    fun `로그인 실패 - 유효성`() = runTest {
        every { ValidationUtils.validateEmail(any<String>(), any(), any()) } returns ValidationResult.Invalid(R.string.error_email_invalid)
        every { ValidationUtils.validatePassword(any<String>(), any(), any()) } returns ValidationResult.Valid
        createViewModel()
        viewModel.test(
            this,
            initialState = LoginUiState(
                email = TextFieldValue("bademail"),
                password = TextFieldValue("pw123!")
            )
        ) {
            viewModel.onLoginClicked()
            awaitState().run { assert(emailError == R.string.error_email_invalid) }
            expectSideEffect(LoginSideEffect.ShowError(R.string.error_email_invalid))
        }
        coVerify(exactly = 0) { mockLoginUseCase(any(), any()) }
    }

    @Test
    fun `로그인 Exception 실패시 사이드이펙트`() = runTest {
        every { ValidationUtils.validateEmail(any<String>(), any(), any()) } returns ValidationResult.Valid
        every { ValidationUtils.validatePassword(any<String>(), any(), any()) } returns ValidationResult.Valid
        coEvery { mockLoginUseCase("x@x.com", "pw!") } throws Exception("fail")
        createViewModel()
        viewModel.test(
            this,
            initialState = LoginUiState(
                email = TextFieldValue("x@x.com"),
                password = TextFieldValue("pw!")
            )
        ) {
            viewModel.onLoginClicked()
            awaitState()
            awaitState()
            expectSideEffect(LoginSideEffect.ShowError(R.string.login_failed))
        }
        coVerify { mockLoginUseCase("x@x.com", "pw!") }
    }
}
