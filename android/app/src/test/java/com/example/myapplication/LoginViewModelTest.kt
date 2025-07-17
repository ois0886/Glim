package com.example.myapplication

import app.cash.turbine.test
import com.example.myapplication.core.navigation.Navigator
import com.example.myapplication.core.navigation.Route
import com.example.myapplication.feature.auth.login.LoginSideEffect
import com.example.myapplication.feature.auth.login.LoginViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class LoginViewModelTest {
    // fake Navigator
    private class FakeNavigator : Navigator {
        override suspend fun navigate(
            route: Route,
            saveState: Boolean,
            launchSingleTop: Boolean,
        ) { /* no-op */ }

        override suspend fun navigateBack() {
        /* no-op */ }
    }

    @Test
    fun `이메일 유효성 검사 테스트`() =
        runTest {
            val viewModel = LoginViewModel(FakeNavigator())

            viewModel.container.stateFlow.test {
                awaitItem() // initial

                viewModel.onEmailChanged("invalid-email")

                val state = awaitItem()
                assertEquals("invalid-email", state.email)
                assertEquals("유효한 이메일을 입력해주세요.", state.emailError)
            }
        }

    @Test
    fun `비밀번호 유효성 검사 테스트`() =
        runTest {
            val viewModel = LoginViewModel(FakeNavigator())

            viewModel.container.stateFlow.test {
                awaitItem()
                viewModel.onPasswordChanged("short")

                val state = awaitItem()
                assertEquals("short", state.password)
                assertEquals("대소문자·숫자·특수문자 포함 8~16자", state.passwordError)
            }
        }

    @Test
    fun `빈 자격증명 로그인 시 에러 발생`() =
        runTest {
            val viewModel = LoginViewModel(FakeNavigator())

            // 상태 흐름 검증
            viewModel.container.stateFlow.test {
                awaitItem()
                viewModel.onLoginClicked()

                val state = awaitItem()
                assertEquals("이메일을 입력해주세요.", state.emailError)
                assertEquals("비밀번호를 입력해주세요.", state.passwordError)
            }

            // 사이드 이펙트 흐름 검증
            viewModel.container.sideEffectFlow.test {
                val effect = awaitItem()
                assert(effect is LoginSideEffect.ShowError)
            }
        }
}
