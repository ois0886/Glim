package com.example.myapplication

import app.cash.turbine.test
import com.example.myapplication.feature.auth.signup.SignUpSideEffect
import com.example.myapplication.feature.auth.signup.SignUpStep
import com.example.myapplication.feature.auth.signup.SignUpViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SignUpViewModelTest {
    @Test
    fun `이메일 입력 시 상태 반영`() =
        runTest {
            val viewModel = SignUpViewModel()

            viewModel.container.stateFlow.test {
                awaitItem() // initial

                viewModel.onEmailChanged("test@example.com")

                val state = awaitItem()
                assertEquals("test@example.com", state.email)
                assertEquals(null, state.emailError)
            }
        }

    @Test
    fun `비밀번호 입력 시 상태 반영`() =
        runTest {
            val viewModel = SignUpViewModel()

            viewModel.container.stateFlow.test {
                awaitItem()

                viewModel.onPasswordChanged("Pass123!")

                val state = awaitItem()
                assertEquals("Pass123!", state.password)
                assertEquals(null, state.passwordError)
            }
        }

    @Test
    fun `이메일 미입력 상태에서 다음 단계 시 에러 메시지`() =
        runTest {
            val viewModel = SignUpViewModel()

            viewModel.container.stateFlow.test {
                awaitItem()
                viewModel.onNextStep()
                val state = awaitItem()
                assertEquals("이메일을 입력해주세요.", state.emailError)
            }

            viewModel.container.sideEffectFlow.test {
                val effect = awaitItem()
                assertIs<SignUpSideEffect.ShowToast>(effect)
                assertEquals("이메일을 입력해주세요.", effect.message)
            }
        }

    @Test
    fun `이메일 입력 후 onNextStep 호출 시 단계 이동`() =
        runTest {
            val viewModel = SignUpViewModel()

            viewModel.onEmailChanged("test@example.com")

            viewModel.container.stateFlow.test {
                awaitItem()
                viewModel.onNextStep()
                val state = awaitItem()
                assertEquals(SignUpStep.Code, state.currentStep)
            }
        }

    @Test
    fun `비밀번호 불일치 시 에러 발생`() =
        runTest {
            val viewModel = SignUpViewModel()

            viewModel.onEmailChanged("test@example.com")
            viewModel.onCodeChanged("123456")
            viewModel.onPasswordChanged("password123")
            viewModel.onConfirmPasswordChanged("different123")

            // 이 시점의 currentStep은 Email이므로, 아무리 호출해도 Password 검증이 안 됨
            viewModel.onNextStep() // → Email 단계 → Code 단계
            viewModel.onNextStep() // → Code 단계 → Password 단계
            viewModel.onNextStep() // → 이제 Password 단계 → 여기서 검사됨

            viewModel.container.sideEffectFlow.test {
                val sideEffect = awaitItem()
                assertEquals(SignUpSideEffect.ShowToast("비밀번호가 일치하지 않습니다."), sideEffect)
            }
        }

    @Test
    fun `모든 입력 후 최종 단계에서 메인으로 이동`() =
        runTest {
            val viewModel = SignUpViewModel()

            // simulate 완료 단계까지 이동
            viewModel.onEmailChanged("a@a.com")
            viewModel.onNextStep()
            viewModel.onCodeChanged("1234")
            viewModel.onNextStep()
            viewModel.onPasswordChanged("A1@aaaaa")
            viewModel.onConfirmPasswordChanged("A1@aaaaa")
            viewModel.onNextStep()

            viewModel.container.stateFlow.test {
                repeat(7) { awaitItem() } // consume all intermediate states
                viewModel.onNextStep()

                val loadingState = awaitItem()
                assertEquals(true, loadingState.isLoading)
            }

            viewModel.container.sideEffectFlow.test {
                val effect = awaitItem()
                assertIs<SignUpSideEffect.NavigateToMain>(effect)
            }
        }
}
