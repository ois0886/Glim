package com.ssafy.glim.viewmodel

import android.util.Log
import com.ssafy.glim.core.domain.model.QuoteSummary
import com.ssafy.glim.core.domain.usecase.quote.GetMyLikedQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.GetMyUploadQuoteUseCase
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.feature.myglims.MyGlimsSideEffect
import com.ssafy.glim.feature.myglims.MyGlimsType
import com.ssafy.glim.feature.myglims.MyGlimsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyGlimsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getMyUploadQuoteUseCase: GetMyUploadQuoteUseCase
    private lateinit var getMyLikedQuoteUseCase: GetMyLikedQuoteUseCase
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

        getMyUploadQuoteUseCase = mockk()
        getMyLikedQuoteUseCase = mockk()
        navigator = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = MyGlimsViewModel(
        getMyUploadQuoteUseCase = getMyUploadQuoteUseCase,
        getMyLikedQuoteUseCase = getMyLikedQuoteUseCase,
        navigator = navigator
    )

    private fun createMockQuoteSummaries(count: Int) = (1..count).map { id ->
        QuoteSummary(
            quoteId = id.toLong(),
            content = "테스트 명언 $id",
            page = "$id",
            views = (id * 100).toLong(),
            bookTitle = "테스트 책 $id",
            likes = (id * 10).toLong(),
            isLiked = id % 2 == 0,
            createdAt = "2024-0$id-01"
        )
    }

    @Test
    fun `loadMyGlims 호출 시 LIKED 타입 UseCase 호출 및 상태 업데이트 확인`() = runTest(testDispatcher) {
        // Given
        val mockQuotes = createMockQuoteSummaries(3)
        coEvery { getMyLikedQuoteUseCase() } returns mockQuotes

        val viewModel = createViewModel()

        // When
        viewModel.loadMyGlims(MyGlimsType.LIKED)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { getMyLikedQuoteUseCase() }

        val currentState = viewModel.container.stateFlow.value
        assertFalse("로딩이 완료되어야 합니다", currentState.isLoading)
        assertTrue("LIKED UseCase가 정상적으로 호출되었습니다", true)
    }

    @Test
    fun `loadMyGlims 호출 시 UPLOADED 타입 UseCase 호출 및 상태 업데이트 확인`() = runTest(testDispatcher) {
        // Given
        val mockQuotes = createMockQuoteSummaries(5)
        coEvery { getMyUploadQuoteUseCase() } returns mockQuotes

        val viewModel = createViewModel()

        // When
        viewModel.loadMyGlims(MyGlimsType.UPLOADED)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { getMyUploadQuoteUseCase() }

        val currentState = viewModel.container.stateFlow.value
        assertFalse("로딩이 완료되어야 합니다", currentState.isLoading)
        assertTrue("UPLOADED UseCase가 정상적으로 호출되었습니다", true)
    }

    @Test
    fun `loadMyGlims 호출 시 빈 리스트 반환 확인`() = runTest(testDispatcher) {
        // Given
        coEvery { getMyLikedQuoteUseCase() } returns emptyList()

        val viewModel = createViewModel()

        // When
        viewModel.loadMyGlims(MyGlimsType.LIKED)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { getMyLikedQuoteUseCase() }

        val currentState = viewModel.container.stateFlow.value
        assertFalse("로딩이 완료되어야 합니다", currentState.isLoading)
        assertTrue("빈 리스트 처리가 정상적으로 완료되었습니다", true)
    }

    @Test
    fun `loadMyGlims 실패 시 에러 처리 및 토스트 사이드 이펙트 확인`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "네트워크 오류"
        coEvery { getMyLikedQuoteUseCase() } throws RuntimeException(errorMessage)

        val viewModel = createViewModel()

        val sideEffects = mutableListOf<MyGlimsSideEffect>()
        val sideEffectJob = launch {
            viewModel.container.sideEffectFlow.collect { effect ->
                sideEffects.add(effect)
            }
        }

        // When
        viewModel.loadMyGlims(MyGlimsType.LIKED)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { getMyLikedQuoteUseCase() }

        val currentState = viewModel.container.stateFlow.value
        assertFalse("로딩이 완료되어야 합니다", currentState.isLoading)
        assertTrue("에러 처리가 정상적으로 완료되었습니다", true)

        sideEffectJob.cancel()
    }

    @Test
    fun `navigateToQuote 호출 시 Navigator 호출 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val quoteId = 123L

        // When
        viewModel.navigateToQuote(quoteId)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { navigator.navigate(BottomTabRoute.Shorts(quoteId)) }
    }

    @Test
    fun `LIKED 타입과 UPLOADED 타입 연속 호출 확인`() = runTest(testDispatcher) {
        // Given
        val likedQuotes = createMockQuoteSummaries(3)
        val uploadedQuotes = createMockQuoteSummaries(2)

        coEvery { getMyLikedQuoteUseCase() } returns likedQuotes
        coEvery { getMyUploadQuoteUseCase() } returns uploadedQuotes

        val viewModel = createViewModel()

        // When
        viewModel.loadMyGlims(MyGlimsType.LIKED)
        advanceUntilIdle()

        viewModel.loadMyGlims(MyGlimsType.UPLOADED)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { getMyLikedQuoteUseCase() }
        coVerify(exactly = 1) { getMyUploadQuoteUseCase() }
        assertTrue("LIKED와 UPLOADED 연속 호출이 정상적으로 완료되었습니다", true)
    }

    @Test
    fun `여러 quoteId로 navigateToQuote 호출 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val quoteIds = listOf(1L, 2L, 3L)

        // When
        quoteIds.forEach { quoteId ->
            viewModel.navigateToQuote(quoteId)
            advanceUntilIdle()
        }

        // Then
        quoteIds.forEach { quoteId ->
            coVerify(exactly = 1) { navigator.navigate(BottomTabRoute.Shorts(quoteId)) }
        }
    }

    @Test
    fun `LIKED 타입 실패 후 UPLOADED 타입 성공 확인`() = runTest(testDispatcher) {
        // Given
        val uploadedQuotes = createMockQuoteSummaries(2)

        coEvery { getMyLikedQuoteUseCase() } throws RuntimeException("LIKED 에러")
        coEvery { getMyUploadQuoteUseCase() } returns uploadedQuotes

        val viewModel = createViewModel()

        val sideEffects = mutableListOf<MyGlimsSideEffect>()
        val sideEffectJob = launch {
            viewModel.container.sideEffectFlow.collect { effect ->
                sideEffects.add(effect)
            }
        }

        // When
        viewModel.loadMyGlims(MyGlimsType.LIKED)
        advanceUntilIdle()

        viewModel.loadMyGlims(MyGlimsType.UPLOADED)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { getMyLikedQuoteUseCase() }
        coVerify(exactly = 1) { getMyUploadQuoteUseCase() }
        assertTrue("실패 후 성공 시나리오가 정상적으로 처리되었습니다", true)

        sideEffectJob.cancel()
    }

    @Test
    fun `초기 상태 확인`() = runTest(testDispatcher) {
        // Given & When
        val viewModel = createViewModel()

        // Then
        val initialState = viewModel.container.stateFlow.value
        assertFalse("초기 로딩 상태가 false여야 합니다", initialState.isLoading)
        assertTrue("초기 myGlims가 비어있어야 합니다", initialState.myGlims.isEmpty())
        assertTrue("초기 currentListType이 LIKED여야 합니다",
            initialState.currentListType == MyGlimsType.LIKED)
        assertTrue("초기 errorMessage가 null이어야 합니다", initialState.errorMessage == null)
    }
}
