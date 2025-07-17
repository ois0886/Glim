package com.example.myapplication

import app.cash.turbine.test
import com.example.myapplication.core.navigation.Navigator
import com.example.myapplication.core.navigation.Route
import com.example.myapplication.feature.auth.signup.SignUpStep
import com.example.myapplication.feature.auth.signup.SignUpViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SignUpViewModelTest {
    private class FakeNavigator : Navigator {
        var navigateBackCalled = false

        override suspend fun navigate(
            route: Route,
            saveState: Boolean,
            launchSingleTop: Boolean,
        ) {}

        override suspend fun navigateBack() {
            navigateBackCalled = true
        }
    }

    @Test
    fun `이메일 변경 시 상태 반영`() =
        runTest {
            val vm = SignUpViewModel(FakeNavigator())

            vm.container.stateFlow.test {
                val initial = awaitItem()
                assertEquals("", initial.email)
                assertNull(initial.emailError)

                vm.onEmailChanged("invalid")
                val invalidState = awaitItem()
                assertEquals("invalid", invalidState.email)
                assertEquals("유효한 이메일을 입력해주세요.", invalidState.emailError)

                vm.onEmailChanged("user@example.com")
                val validState = awaitItem()
                assertEquals("user@example.com", validState.email)
                assertNull(validState.emailError)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `코드 필터링 및 유효성 검사`() =
        runTest {
            val vm = SignUpViewModel(FakeNavigator())

            vm.container.stateFlow.test {
                awaitItem() // initial
                vm.onEmailChanged("a@a.com")
                awaitItem() // email set
                vm.onNextStep()
                val codeStep = awaitItem()
                assertEquals(SignUpStep.Code, codeStep.currentStep)

                vm.onCodeChanged("12ab34cd56")
                val filtered = awaitItem()
                assertEquals("123456", filtered.code)
                assertNull(filtered.codeError)

                vm.onCodeChanged("123")
                val shortCode = awaitItem()
                assertEquals("123", shortCode.code)
                assertEquals("6자리 숫자를 입력해주세요.", shortCode.codeError)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `비밀번호 및 확인 불일치 처리`() =
        runTest {
            val vm = SignUpViewModel(FakeNavigator())

            vm.container.stateFlow.test {
                awaitItem()
                // move to Password step
                vm.onEmailChanged("a@a.com")
                awaitItem()
                vm.onNextStep()
                awaitItem()
                vm.onCodeChanged("123456")
                awaitItem()
                vm.onNextStep()
                val passwordStep = awaitItem()
                assertEquals(SignUpStep.Password, passwordStep.currentStep)

                vm.onPasswordChanged("short")
                val pwErr = awaitItem()
                assertTrue(pwErr.passwordError!!.contains("8~16자"))

                vm.onConfirmPasswordChanged("diff")
                val confirmErr = awaitItem()
                assertEquals("비밀번호가 일치하지 않습니다.", confirmErr.confirmPasswordError)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `단계별 이동 동작 확인`() =
        runTest {
            val vm = SignUpViewModel(FakeNavigator())

            vm.container.stateFlow.test {
                val initial = awaitItem()
                assertEquals(SignUpStep.Email, initial.currentStep)

                vm.onNextStep()
                val stillEmail = awaitItem()
                assertEquals(SignUpStep.Email, stillEmail.currentStep)

                vm.onEmailChanged("a@a.com")
                awaitItem()
                vm.onNextStep()
                val toCode = awaitItem()
                assertEquals(SignUpStep.Code, toCode.currentStep)

                vm.onNextStep()
                val stillCode = awaitItem()
                assertEquals(SignUpStep.Code, stillCode.currentStep)

                vm.onCodeChanged("123456")
                awaitItem()
                vm.onNextStep()
                val toPassword = awaitItem()
                assertEquals(SignUpStep.Password, toPassword.currentStep)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `전체 회원가입 완료 흐름`() =
        runTest {
            val vm = SignUpViewModel(FakeNavigator())

            vm.container.stateFlow.test {
                // consume initial state
                awaitItem()

                // Email → Code
                vm.onEmailChanged("a@a.com")
                awaitItem() // email updated
                vm.onNextStep()
                val codeState = awaitItem()
                assertEquals(SignUpStep.Code, codeState.currentStep)

                // Code → Password
                vm.onCodeChanged("111111")
                awaitItem() // code updated
                vm.onNextStep()
                val passwordState = awaitItem()
                assertEquals(SignUpStep.Password, passwordState.currentStep)

                // Password → Profile
                vm.onPasswordChanged("Aa1!aaaa")
                awaitItem() // password updated
                vm.onConfirmPasswordChanged("Aa1!aaaa")
                awaitItem() // confirm password updated
                vm.onNextStep()
                val profileState = awaitItem()
                assertEquals(SignUpStep.Profile, profileState.currentStep)

                // fill profile fields
                vm.onNameChanged("홍길동")
                awaitItem() // name updated
                vm.onBirthYearChanged("1990")
                awaitItem() // birth year updated
                vm.onGenderSelected("M")
                val genderState = awaitItem()
                assertEquals("M", genderState.gender)

                // final next (no more events)
                vm.onNextStep()
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `초기 단계에서 뒤로가기 호출`() =
        runTest {
            val navigator = FakeNavigator()
            val vm = SignUpViewModel(navigator)

            vm.container.stateFlow.test {
                awaitItem()
                vm.onBackStep()
                // no new state emitted, just check navigator
                cancelAndIgnoreRemainingEvents()
            }

            assertTrue(navigator.navigateBackCalled)
        }
}
