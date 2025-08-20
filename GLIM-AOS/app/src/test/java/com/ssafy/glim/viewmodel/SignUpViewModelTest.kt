package com.ssafy.glim.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.VerifyEmail
import com.ssafy.glim.core.domain.usecase.auth.SignUpUseCase
import com.ssafy.glim.core.domain.usecase.auth.VerifyEmailUseCase
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.feature.auth.signup.SignUpSideEffect
import com.ssafy.glim.feature.auth.signup.SignUpStep
import com.ssafy.glim.feature.auth.signup.SignUpViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.test.test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val navigator = mockk<Navigator>(relaxed = true)
    private val signUpUseCase = mockk<SignUpUseCase>()
    private val verifyEmailUseCase = mockk<VerifyEmailUseCase>()
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        viewModel = SignUpViewModel(navigator, signUpUseCase, verifyEmailUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    // 입력 검증

    @Test
    fun 이메일_변경시_검증에_따라_에러반영() = runTest(dispatcher) {
        viewModel.test(this) {
            viewModel.onEmailChanged(TextFieldValue("abc"))
            val s1 = awaitState()
            assertEquals("abc", s1.email.text)
            assertTrue(s1.emailError != null)

            viewModel.onEmailChanged(TextFieldValue("test@example.com"))
            val s2 = awaitState()
            assertEquals("test@example.com", s2.email.text)
            assertNull(s2.emailError)
        }
    }

    @Test
    fun 인증코드_변경시_검증에_따라_에러반영() = runTest(dispatcher) {
        viewModel.test(this) {
            viewModel.onCodeChanged(TextFieldValue("12"))
            val s2 = awaitState()
            assertTrue(s2.codeError != null)

            viewModel.onCodeChanged(TextFieldValue("123456"))
            val s3 = awaitState()
            assertNull(s3.codeError)
        }
    }

    @Test
    fun 비밀번호_변경시_검증_갱신() = runTest(dispatcher) {
        viewModel.test(this) {
            viewModel.onPasswordChanged(TextFieldValue("Abcd1234!"))
            val s1 = awaitState()
            assertEquals("Abcd1234!", s1.password.text)
            assertNull(s1.passwordError)

            viewModel.onConfirmPasswordChanged(TextFieldValue("Abcd1234!"))
            val s2 = awaitState()
            assertEquals("Abcd1234!", s2.confirmPassword.text)
            assertNull(s2.confirmPasswordError)
        }
    }

    @Test
    fun 이름_변경시_검증에_따라_에러반영() = runTest(dispatcher) {
        viewModel.test(this) {
            viewModel.onNameChanged(TextFieldValue("홍길동홍길동홍길동홍길동홍길동홍길동홍길동홍길동홍길동홍길동"))
            val s1 = awaitState()
            assertTrue(s1.nameError != null)

            viewModel.onNameChanged(TextFieldValue("홍길동"))
            val s2 = awaitState()
            assertNull(s2.nameError)
        }
    }

    @Test
    fun 생년월일_변경시_검증에_따라_에러반영() = runTest(dispatcher) {
        viewModel.test(this) {
            viewModel.onBirthChanged(TextFieldValue("1990-01-0x"))
            val s1 = awaitState()
            assertTrue(s1.birthDateError != null)

            viewModel.onBirthChanged(TextFieldValue("19900101"))
            val s2 = awaitState()
            assertNull(s2.birthDateError)
        }
    }

    @Test
    fun 성별_선택시_상태반영() = runTest(dispatcher) {
        viewModel.test(this) {
            viewModel.onGenderSelected("MALE")
            val s = awaitState()
            assertEquals("MALE", s.gender)
        }
    }

    // 약관 토글

    @Test
    fun 전체동의_토글시_동기화() = runTest(dispatcher) {
        viewModel.test(this) {
            viewModel.onToggleAll(true)
            val s1 = awaitState()
            assertTrue(s1.allAgree && s1.termsAgree && s1.privacyAgree && s1.marketingAgree)

            viewModel.onToggleAll(false)
            val s2 = awaitState()
            assertFalse(s2.allAgree || s2.termsAgree || s2.privacyAgree || s2.marketingAgree)
        }
    }

    @Test
    fun 개별동의_토글시_전체계산() = runTest(dispatcher) {
        viewModel.test(this) {
            viewModel.onToggleTerms(true)
            awaitState()
            viewModel.onTogglePrivacy(true)
            awaitState()
            viewModel.onToggleMarketing(true)
            val s1 = awaitState()
            assertTrue(s1.allAgree)

            viewModel.onToggleMarketing(false)
            val s2 = awaitState()
            assertFalse(s2.allAgree)
        }
    }

    // 단계 진행

    @Test
    fun 약관_없으면_토스트() = runTest(dispatcher) {
        val ctx = mockk<Context>(relaxed = true)
        viewModel.test(this) {
            viewModel.onNextStep(ctx)
            expectSideEffect(SignUpSideEffect.ShowToast(R.string.error_terms_required))
        }
    }

    @Test
    fun 약관_있으면_Email() = runTest(dispatcher) {
        val ctx = mockk<Context>(relaxed = true)
        viewModel.test(this) {
            viewModel.onToggleTerms(true)
            awaitState()
            viewModel.onTogglePrivacy(true)
            awaitState()
            viewModel.onNextStep(ctx)
            val s = awaitState()
            assertEquals(SignUpStep.Email, s.currentStep)
        }
    }

    // 이메일 인증

    @Test
    fun 이메일_전송성공() = runTest(dispatcher) {
        val ctx = mockk<Context>(relaxed = true)
        coEvery { verifyEmailUseCase("test@example.com") } returns VerifyEmail(
            message = "ok",
            email = "test@example.com",
            verificationCode = "654321"
        )

        viewModel.test(this) {
            viewModel.onToggleTerms(true)
            awaitState()
            viewModel.onTogglePrivacy(true)
            awaitState()
            viewModel.onNextStep(ctx)
            awaitState()

            viewModel.onEmailChanged(TextFieldValue("test@example.com"))
            awaitState()
            viewModel.onNextStep(ctx) // send

            val loadingState = awaitState()
            assertTrue(loadingState.isLoading)
            advanceUntilIdle()

            val s = awaitState()
            assertFalse(s.isLoading)
            assertEquals("654321", s.actualVerificationCode)

            expectSideEffect(SignUpSideEffect.ShowToast(R.string.verification_code_instruction))
            val finalState = awaitState()
            assertEquals(SignUpStep.Code, finalState.currentStep)
        }
    }

    @Test
    fun 이메일_전송실패() = runTest(dispatcher) {
        val ctx = mockk<Context>(relaxed = true)
        coEvery { verifyEmailUseCase("fail@test.com") } throws RuntimeException("fail")

        viewModel.test(this) {
            viewModel.onToggleTerms(true)
            awaitState()
            viewModel.onTogglePrivacy(true)
            awaitState()
            viewModel.onNextStep(ctx)
            awaitState()

            viewModel.onEmailChanged(TextFieldValue("fail@test.com"))
            awaitState()
            viewModel.onNextStep(ctx)

            val loadingState = awaitState()
            assertTrue(loadingState.isLoading)
            advanceUntilIdle()
            val finalState = awaitState()
            assertFalse(finalState.isLoading)

            expectSideEffect(SignUpSideEffect.ShowToast(R.string.verification_code_failed))
        }
    }

    // 코드 검증

    @Test
    fun 코드_일치_다음단계() = runTest(dispatcher) {
        val ctx = mockk<Context>(relaxed = true)
        coEvery { verifyEmailUseCase("a@a.com") } returns VerifyEmail(
            message = "ok",
            email = "a@a.com",
            verificationCode = "111111"
        )

        viewModel.test(this) {
            viewModel.onToggleTerms(true)
            awaitState()
            viewModel.onTogglePrivacy(true)
            awaitState()
            viewModel.onNextStep(ctx)
            awaitState()

            viewModel.onEmailChanged(TextFieldValue("a@a.com"))
            awaitState()
            viewModel.onNextStep(ctx) // send
            awaitState()
            advanceUntilIdle()
            awaitState()

            expectSideEffect(SignUpSideEffect.ShowToast(R.string.verification_code_instruction))
            val afterSend = awaitState()
            assertEquals(SignUpStep.Code, afterSend.currentStep)
            assertEquals("111111", afterSend.actualVerificationCode)

            viewModel.onCodeChanged(TextFieldValue("111111"))
            awaitState()
            viewModel.onNextStep(ctx)

            expectSideEffect(SignUpSideEffect.ShowToast(R.string.signup_verify_code))
            val finalState = awaitState()
            assertEquals(SignUpStep.Password, finalState.currentStep)
        }
    }
    // 뒤로가기

    @Test
    fun 이전단계_뒤로가기() = runTest(dispatcher) {
        val ctx = mockk<Context>(relaxed = true)
        viewModel.test(this) {
            viewModel.onToggleTerms(true)
            awaitState()
            viewModel.onTogglePrivacy(true)
            awaitState()
            viewModel.onNextStep(ctx)
            awaitState()
            viewModel.onBackStep()
            val s = awaitState()
            assertEquals(SignUpStep.Terms, s.currentStep)
            viewModel.onBackStep()
        }
        coVerify { navigator.navigateBack() }
    }
}
