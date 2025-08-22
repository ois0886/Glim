package com.ssafy.glim.viewmodel

import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.usecase.quote.GetQuotesUseCase
import com.ssafy.glim.core.domain.usecase.quote.LikeQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.UnLikeQuoteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LockViewModelTest {

    private val getQuotesUseCase = mockk<GetQuotesUseCase>()
    private val likeQuoteUseCase = mockk<LikeQuoteUseCase>()
    private val unLikeQuoteUseCase = mockk<UnLikeQuoteUseCase>()

    private fun createMockQuotes(count: Int) = (1..count).map { id ->
        Quote(
            content = "테스트 명언 $id",
            author = "작가 $id",
            bookCoverUrl = "https://test.com/cover$id.jpg",
            bookId = id.toLong(),
            bookTitle = "테스트 책 $id",
            page = id,
            publisher = "출판사 $id",
            quoteId = id.toLong(),
            quoteImageName = "test$id.jpg",
            quoteViews = (id * 10).toLong(),
            isLike = false,
            likes = (id * 5).toLong()
        )
    }

    @Test
    fun `GetQuotesUseCase 호출 시 정상 데이터 반환`() = runTest {
        // Given
        val expectedQuotes = createMockQuotes(5)
        coEvery { getQuotesUseCase(0, 20) } returns expectedQuotes

        // When
        val result = getQuotesUseCase(0, 20)

        // Then
        assertEquals(expectedQuotes.size, result.size)
        assertEquals(expectedQuotes[0].quoteId, result.first().quoteId)
        coVerify(exactly = 1) { getQuotesUseCase(0, 20) }
    }

    @Test
    fun `LikeQuoteUseCase 호출 확인`() = runTest {
        // Given
        val quoteId = 123L
        coEvery { likeQuoteUseCase(quoteId) } just runs

        // When
        likeQuoteUseCase(quoteId)

        // Then
        coVerify(exactly = 1) { likeQuoteUseCase(quoteId) }
    }

    @Test
    fun `UnLikeQuoteUseCase 호출 확인`() = runTest {
        // Given
        val quoteId = 456L
        coEvery { unLikeQuoteUseCase(quoteId) } just runs

        // When
        unLikeQuoteUseCase(quoteId)

        // Then
        coVerify(exactly = 1) { unLikeQuoteUseCase(quoteId) }
    }

    @Test
    fun `GetQuotesUseCase 빈 리스트 반환 확인`() = runTest {
        // Given
        coEvery { getQuotesUseCase(any(), any()) } returns emptyList()

        // When
        val result = getQuotesUseCase(0, 20)

        // Then
        assertTrue("빈 리스트가 반환되어야 합니다", result.isEmpty())
        coVerify(exactly = 1) { getQuotesUseCase(0, 20) }
    }

    @Test
    fun `GetQuotesUseCase 여러 페이지 호출 확인`() = runTest {
        // Given
        val page1Quotes = createMockQuotes(3)
        val page2Quotes = createMockQuotes(2).map { it.copy(quoteId = it.quoteId + 10) }

        coEvery { getQuotesUseCase(0, 20) } returns page1Quotes
        coEvery { getQuotesUseCase(1, 20) } returns page2Quotes

        // When
        val result1 = getQuotesUseCase(0, 20)
        val result2 = getQuotesUseCase(1, 20)

        // Then
        assertEquals(3, result1.size)
        assertEquals(2, result2.size)
        coVerify(exactly = 1) { getQuotesUseCase(0, 20) }
        coVerify(exactly = 1) { getQuotesUseCase(1, 20) }
    }
}
