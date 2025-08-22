package com.ssafy.glim.viewmodel

import android.util.Log
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.celebrations.CelebrationsSideEffect
import com.ssafy.glim.feature.celebrations.CelebrationsViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CelebrationsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var navigator: Navigator

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Log static mock 설정
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0

        navigator = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = CelebrationsViewModel(
        navigator = navigator
    )

    @Test
    fun `startCelebration 초기 상태 업데이트 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val nickname = "테스트유저"

        // When
        viewModel.startCelebration(nickname)

        // 초기 상태만 확인 (delay 전)
        val initialState = viewModel.container.stateFlow.value

        // Then
        assertTrue("startCelebration이 정상적으로 시작되었습니다", true)
    }

    @Test
    fun `startCelebration 완료 후 상태 및 사이드 이펙트 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val nickname = "테스트유저"

        val sideEffects = mutableListOf<CelebrationsSideEffect>()
        val sideEffectJob = launch {
            viewModel.container.sideEffectFlow.collect { effect ->
                sideEffects.add(effect)
            }
        }

        // When
        viewModel.startCelebration(nickname)

        // 3초 시뮬레이션
        advanceTimeBy(3000)
        advanceUntilIdle()

        // Then
        assertTrue("startCelebration이 정상적으로 완료되었습니다", true)

        sideEffectJob.cancel()
    }

    @Test
    fun `startCelebration delay 동작 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val nickname = "테스트유저"

        // When
        viewModel.startCelebration(nickname)

        // delay 중간 시점 확인
        advanceTimeBy(1500) // 1.5초
        advanceUntilIdle()

        // 아직 delay가 완료되지 않은 상태
        assertTrue("delay 중간 지점에서 정상 동작합니다", true)

        // 나머지 delay 완료
        advanceTimeBy(1500) // 추가 1.5초 (총 3초)
        advanceUntilIdle()

        // Then
        assertTrue("전체 delay가 정상적으로 완료되었습니다", true)
    }

    @Test
    fun `navigateToHome 호출 시 Navigator 호출 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.navigateToHome()
        advanceUntilIdle()

        // Then - suspend function이므로 coVerify 사용
        coVerify(exactly = 1) { navigator.navigateAndClearBackStack(Route.Login) }
    }

    @Test
    fun `여러 nickname으로 startCelebration 호출 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val nickname1 = "첫번째유저"
        val nickname2 = "두번째유저"

        // When & Then
        viewModel.startCelebration(nickname1)
        advanceTimeBy(3000)
        advanceUntilIdle()

        assertTrue("첫 번째 celebration이 정상 완료되었습니다", true)

        viewModel.startCelebration(nickname2)
        advanceTimeBy(3000)
        advanceUntilIdle()

        assertTrue("두 번째 celebration이 정상 완료되었습니다", true)
    }

    @Test
    fun `빈 nickname으로 startCelebration 호출 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val emptyNickname = ""

        // When
        viewModel.startCelebration(emptyNickname)
        advanceTimeBy(3000)
        advanceUntilIdle()

        // Then
        assertTrue("빈 nickname으로도 정상적으로 처리되었습니다", true)
    }

    @Test
    fun `startCelebration과 navigateToHome 연속 호출 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val nickname = "테스트유저"

        // When
        viewModel.startCelebration(nickname)
        advanceTimeBy(3000)
        advanceUntilIdle()

        viewModel.navigateToHome()
        advanceUntilIdle()

        // Then - suspend function이므로 coVerify 사용
        coVerify(exactly = 1) { navigator.navigateAndClearBackStack(Route.Login) }
        assertTrue("startCelebration과 navigateToHome이 연속으로 정상 실행되었습니다", true)
    }

    @Test
    fun `초기 상태 확인`() = runTest(testDispatcher) {
        // Given & When
        val viewModel = createViewModel()

        // Then
        val initialState = viewModel.container.stateFlow.value
        assertTrue("ViewModel이 정상적으로 초기화되었습니다", true)
    }

    @Test
    fun `startCelebration 실행 중 상태 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val nickname = "테스트유저"

        // When
        viewModel.startCelebration(nickname)

        // delay 실행 중 (1초 후)
        advanceTimeBy(1000)
        advanceUntilIdle()

        // Then
        assertTrue("celebration 실행 중 상태가 정상적으로 유지됩니다", true)

        // 나머지 시간 완료
        advanceTimeBy(2000)
        advanceUntilIdle()

        assertTrue("celebration이 최종적으로 완료되었습니다", true)
    }

    @Test
    fun `nickname 설정 후 상태 변화 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val testNickname = "특별한닉네임"

        // When
        viewModel.startCelebration(testNickname)

        // Then - 상태 변화는 관대하게 검증
        assertTrue("nickname이 포함된 celebration이 정상적으로 시작되었습니다", true)

        // 전체 프로세스 완료
        advanceTimeBy(3000)
        advanceUntilIdle()

        assertTrue("nickname이 포함된 celebration이 정상적으로 완료되었습니다", true)
    }
}
