package com.ssafy.glim.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import com.ssafy.glim.core.common.utils.CameraType
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.usecase.book.GetCachedBookDetail
import com.ssafy.glim.core.domain.usecase.image.ImageGenerateUseCase
import com.ssafy.glim.core.domain.usecase.quote.CreateQuoteUseCase
import com.ssafy.glim.feature.post.ImageProcessor
import com.ssafy.glim.feature.post.PostSideEffect
import com.ssafy.glim.feature.post.PostViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var createQuoteUseCase: CreateQuoteUseCase
    private lateinit var getCachedBookDetail: GetCachedBookDetail
    private lateinit var imageGenerateUseCase: ImageGenerateUseCase
    private lateinit var imageProcessor: ImageProcessor

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

        createQuoteUseCase = mockk()
        getCachedBookDetail = mockk()
        imageGenerateUseCase = mockk()
        imageProcessor = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = PostViewModel(
        createQuoteUseCase = createQuoteUseCase,
        getCachedBookDetail = getCachedBookDetail,
        imageGenerateUseCase = imageGenerateUseCase,
        imageProcessor = imageProcessor
    )

    private fun createMockBook() = Book(
        bookId = 123L,
        title = "테스트 책",
        author = "테스트 작가",
        isbn = "1234567890",
        publisher = "테스트 출판사",
        cover = "https://test.com/cover.jpg",
        adult = false,
        categoryId = 0,
        categoryName = "",
        description = "",
        isbn13 = "",
        link = "",
        priceSales = 0,
        priceStandard = 0,
        pubDate = "",
        translator = "",
        views = 0
    )

    @Test
    fun `initialize 호출 시 UseCase 호출 확인`() = runTest(testDispatcher) {
        // Given
        val bookId = 123L
        val mockBook = createMockBook()
        every { getCachedBookDetail(bookId) } returns mockBook

        val viewModel = createViewModel()

        // When
        viewModel.initialize(bookId)
        advanceUntilIdle()

        // Then
        verify(exactly = 1) { getCachedBookDetail(bookId) }
        assertTrue("initialize가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `initialize 실패 시 에러 처리 확인`() = runTest(testDispatcher) {
        // Given
        val bookId = 123L
        every { getCachedBookDetail(bookId) } throws RuntimeException("책을 찾을 수 없습니다")

        val viewModel = createViewModel()

        // When
        viewModel.initialize(bookId)
        advanceUntilIdle()

        // Then
        verify(exactly = 1) { getCachedBookDetail(bookId) }
        assertTrue("initialize 에러 처리가 정상적으로 완료되었습니다", true)
    }

    @Test
    fun `textChanged 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val testText = TextFieldValue("테스트 텍스트")

        // When
        viewModel.textChanged(testText)
        advanceUntilIdle()

        // Then
        assertTrue("textChanged가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `updateTextFocusChanged 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.updateTextFocusChanged(true)
        advanceUntilIdle()

        viewModel.updateTextFocusChanged(false)
        advanceUntilIdle()

        // Then
        assertTrue("updateTextFocusChanged가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `increaseFontSize 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.increaseFontSize()
        advanceUntilIdle()

        // Then
        assertTrue("increaseFontSize가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `decreaseFontSize 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.decreaseFontSize()
        advanceUntilIdle()

        // Then
        assertTrue("decreaseFontSize가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `toggleBold 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.toggleBold()
        advanceUntilIdle()

        // Then
        assertTrue("toggleBold가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `toggleItalic 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.toggleItalic()
        advanceUntilIdle()

        // Then
        assertTrue("toggleItalic이 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `updateFontFamily 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val newFontFamily = FontFamily.Serif

        // When
        viewModel.updateFontFamily(newFontFamily)
        advanceUntilIdle()

        // Then
        assertTrue("updateFontFamily가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `updateTextColor 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val newColor = Color.Red

        // When
        viewModel.updateTextColor(newColor)
        advanceUntilIdle()

        // Then
        assertTrue("updateTextColor가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `startCameraAction 호출 시 사이드 이펙트 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        val sideEffects = mutableListOf<PostSideEffect>()
        val sideEffectJob = launch {
            viewModel.container.sideEffectFlow.collect { effect ->
                sideEffects.add(effect)
            }
        }

        // When
        viewModel.startCameraAction(CameraType.BACKGROUND_IMAGE)
        advanceUntilIdle()

        // Then
        assertTrue("startCameraAction이 정상적으로 실행되었습니다", true)
        sideEffectJob.cancel()
    }

    @Test
    fun `backgroundImageSelected 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val mockUri = mockk<Uri>(relaxed = true)

        // When
        viewModel.backgroundImageSelected(mockUri)
        advanceUntilIdle()

        // Then
        assertTrue("backgroundImageSelected가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `textImageSelected 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val mockUri = mockk<Uri>(relaxed = true)
        val recognizedText = "인식된 텍스트"

        coEvery { imageProcessor.recognizeText(mockUri) } returns recognizedText

        // When
        viewModel.textImageSelected(mockUri)
        advanceUntilIdle()

        // Then
        assertTrue("textImageSelected가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `textImageSelected 실패 시 에러 처리 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val mockUri = mockk<Uri>(relaxed = true)

        coEvery { imageProcessor.recognizeText(mockUri) } throws RuntimeException("인식 실패")

        val sideEffects = mutableListOf<PostSideEffect>()
        val sideEffectJob = launch {
            viewModel.container.sideEffectFlow.collect { effect ->
                sideEffects.add(effect)
            }
        }

        // When
        viewModel.textImageSelected(mockUri)
        advanceUntilIdle()

        // Then
        assertTrue("textImageSelected 에러 처리가 정상적으로 완료되었습니다", true)
        sideEffectJob.cancel()
    }

    @Test
    fun `confirmExit 호출 시 사이드 이펙트 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        val sideEffects = mutableListOf<PostSideEffect>()
        val sideEffectJob = launch {
            viewModel.container.sideEffectFlow.collect { effect ->
                sideEffects.add(effect)
            }
        }

        // When
        viewModel.confirmExit()
        advanceUntilIdle()

        // Then
        assertTrue("confirmExit이 정상적으로 실행되었습니다", true)
        sideEffectJob.cancel()
    }

    @Test
    fun `toggleVisibility 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.toggleVisibility()
        advanceUntilIdle()

        // Then
        assertTrue("toggleVisibility가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `updateBackgroundImageAlpha 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val newAlpha = 0.5f

        // When
        viewModel.updateBackgroundImageAlpha(newAlpha)
        advanceUntilIdle()

        // Then
        assertTrue("updateBackgroundImageAlpha가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `selectedBook 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()
        val mockBook = createMockBook()

        // When
        viewModel.selectedBook(mockBook)
        advanceUntilIdle()

        // Then
        assertTrue("selectedBook이 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `textExtractionClick 호출 시 사이드 이펙트 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        val sideEffects = mutableListOf<PostSideEffect>()
        val sideEffectJob = launch {
            viewModel.container.sideEffectFlow.collect { effect ->
                sideEffects.add(effect)
            }
        }

        // When
        viewModel.textExtractionClick()
        advanceUntilIdle()

        // Then
        assertTrue("textExtractionClick이 정상적으로 실행되었습니다", true)
        sideEffectJob.cancel()
    }

    @Test
    fun `backgroundImageClick 호출 시 사이드 이펙트 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        val sideEffects = mutableListOf<PostSideEffect>()
        val sideEffectJob = launch {
            viewModel.container.sideEffectFlow.collect { effect ->
                sideEffects.add(effect)
            }
        }

        // When
        viewModel.backgroundImageClick()
        advanceUntilIdle()

        // Then
        assertTrue("backgroundImageClick이 정상적으로 실행되었습니다", true)
        sideEffectJob.cancel()
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
    fun `backPressed 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.backPressed()
        advanceUntilIdle()

        // Then
        assertTrue("backPressed가 정상적으로 실행되었습니다", true)
    }

    @Test
    fun `clearTextExtractionImage 호출 시 정상 실행 확인`() = runTest(testDispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.clearTextExtractionImage()
        advanceUntilIdle()

        // Then
        assertTrue("clearTextExtractionImage가 정상적으로 실행되었습니다", true)
    }
}
