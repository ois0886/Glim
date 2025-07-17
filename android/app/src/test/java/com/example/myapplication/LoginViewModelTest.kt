package com.example.myapplication

import app.cash.turbine.test
import com.example.myapplication.core.navigation.Navigator
import com.example.myapplication.core.navigation.Route
import com.example.myapplication.feature.auth.login.LoginSideEffect
import com.example.myapplication.feature.auth.login.LoginViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoginViewModelTest {
    // fake Navigator (추후 네비게이션 테스트용으로 확장 가능)
    private class FakeNavigator : Navigator {
        val calls = mutableListOf<Route>()

        override suspend fun navigate(
            route: Route,
            saveState: Boolean,
            launchSingleTop: Boolean,
        ) {
            calls += route
        }

        override suspend fun navigateBack() { // no-op
        }
    }

    @Test
    fun `이메일 변경 - 빈 문자열일 때 에러`() =
        runTest {
            val viewModel = LoginViewModel(FakeNavigator())

            viewModel.container.stateFlow.test {
                awaitItem() // initial
                viewModel.onEmailChanged("")

                val state = awaitItem()
                assertEquals("", state.email)
                assertEquals("이메일을 입력해주세요.", state.emailError)
            }
        }

    @Test
    fun `이메일 변경 - 유효한 이메일일 때 에러 없음`() =
        runTest {
            val viewModel = LoginViewModel(FakeNavigator())

            viewModel.container.stateFlow.test {
                awaitItem()
                viewModel.onEmailChanged("test@example.com")

                val state = awaitItem()
                assertEquals("test@example.com", state.email)
                assertNull(state.emailError)
            }
        }

    @Test
    fun `비밀번호 변경 - 빈 문자열일 때 에러`() =
        runTest {
            val viewModel = LoginViewModel(FakeNavigator())

            viewModel.container.stateFlow.test {
                awaitItem()
                viewModel.onPasswordChanged("")

                val state = awaitItem()
                assertEquals("", state.password)
                assertEquals("비밀번호를 입력해주세요.", state.passwordError)
            }
        }

    @Test
    fun `비밀번호 변경 - 유효한 패스워드일 때 에러 없음`() =
        runTest {
            val viewModel = LoginViewModel(FakeNavigator())

            // 예: 대문자, 소문자, 숫자, 특수문자 포함 8~16자
            val valid = "Aa1!aaaa"
            viewModel.container.stateFlow.test {
                awaitItem()
                viewModel.onPasswordChanged(valid)

                val state = awaitItem()
                assertEquals(valid, state.password)
                assertNull(state.passwordError)
            }
        }

    @Test
    fun `빈 자격 증명으로 로그인 클릭 시 이메일·패스워드 에러, 사이드 이펙트 발생`() =
        runTest {
            val viewModel = LoginViewModel(FakeNavigator())

            viewModel.container.stateFlow.test {
                awaitItem()
                viewModel.onLoginClicked()

                val state = awaitItem()
                assertEquals("이메일을 입력해주세요.", state.emailError)
                assertEquals("비밀번호를 입력해주세요.", state.passwordError)
            }

            viewModel.container.sideEffectFlow.test {
                val effect = awaitItem()
                assertTrue(effect is LoginSideEffect.ShowError)
            }
        }

    @Test
    fun `유효한 자격 증명으로 로그인 클릭 시 로딩 상태로 변경, 에러 없음`() =
        runTest {
            val viewModel = LoginViewModel(FakeNavigator())

            viewModel.container.stateFlow.test {
                awaitItem()
                viewModel.onEmailChanged("user@example.com")
                awaitItem()
                viewModel.onPasswordChanged("Aa1!aaaa")
                awaitItem()

                viewModel.onLoginClicked()
                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)
                assertNull(loadingState.emailError)
                assertNull(loadingState.passwordError)
            }

            // 로그인 성공 경로에는 사이드이펙트가 없으므로 아무 것도 발생하지 않아야 함
            viewModel.container.sideEffectFlow.test {
                // delay(1_000) 내부에서 아무런 sideEffect를 발생시키지 않기 때문에
                expectNoEvents()
            }
        }
}
