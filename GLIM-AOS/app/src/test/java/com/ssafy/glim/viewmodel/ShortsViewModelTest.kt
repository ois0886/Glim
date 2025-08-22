package com.ssafy.glim.feature.shorts

import android.util.Log
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.usecase.quote.GetQuoteByIdUseCase
import com.ssafy.glim.core.domain.usecase.quote.GetQuotesUseCase
import com.ssafy.glim.core.domain.usecase.quote.LikeQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.UnLikeQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.UpdateQuoteViewCountUseCase
import com.ssafy.glim.core.domain.usecase.shortlink.ShortenUrlUseCase
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.util.ShareManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.test.test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShortsViewModelTest {

    private val getQuotesUseCase: GetQuotesUseCase = mockk()
    private val updateQuoteViewCountUseCase: UpdateQuoteViewCountUseCase = mockk(relaxed = true)
    private val getQuoteByIdUseCase: GetQuoteByIdUseCase = mockk()
    private val likeQuoteUseCase: LikeQuoteUseCase = mockk(relaxed = true)
    private val unLikeQuoteUseCase: UnLikeQuoteUseCase = mockk(relaxed = true)
    private val shortLinkUseCase: ShortenUrlUseCase = mockk()
    private val shareManager: ShareManager = mockk()
    private val navigator: Navigator = mockk(relaxed = true)

    private lateinit var viewModel: ShortsViewModel

    private val quote = Quote(
        quoteId = 1L,
        content = "Sample quote",
        author = "Author",
        bookTitle = "Book Title",
        bookCoverUrl = "http://cover",
        bookId = 100L,
        page = 10,
        publisher = "pub",
        quoteImageName = "img.jpg",
        quoteViews = 99L,
        isLike = false,
        likes = 7L
    )

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.i(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.v(any(), any<String>()) } returns 0

        viewModel = ShortsViewModel(
            getQuotesUseCase, updateQuoteViewCountUseCase, getQuoteByIdUseCase,
            likeQuoteUseCase, unLikeQuoteUseCase, shortLinkUseCase,
            shareManager, navigator
        )
    }

    @Test
    fun toggleLike_whenNotLiked_shouldIncreaseLike() = runTest {
        coEvery { likeQuoteUseCase(1L) } just Runs

        viewModel.test(this, ShortsState(quotes = listOf(quote), currentIdx = 0)) {
            containerHost.toggleLike()
            val newState = awaitState()
            assertTrue(newState.quotes[0].isLike)
            assertEquals(8L, newState.quotes[0].likes)
            coVerify { likeQuoteUseCase(1L) }
        }
    }

    @Test
    fun toggleLike_whenLiked_shouldDecreaseLike() = runTest {
        val likedQuote = quote.copy(isLike = true, likes = 27)
        coEvery { unLikeQuoteUseCase(1L) } just Runs

        viewModel.test(this, ShortsState(quotes = listOf(likedQuote), currentIdx = 0)) {
            containerHost.toggleLike()
            val newState = awaitState()
            assertTrue(!newState.quotes[0].isLike)
            assertEquals(26L, newState.quotes[0].likes)
            coVerify { unLikeQuoteUseCase(1L) }
        }
    }

    @Test
    fun toggleLike_withoutQuote_shouldShowErrorToast() = runTest {
        viewModel.test(this, ShortsState(quotes = emptyList())) {
            containerHost.toggleLike()
            expectSideEffect(ShortsSideEffect.ShowToast("오류 발생"))
        }
    }

    @Test
    fun onShareClick_shortlinkSuccessful_shouldPostShortUrl() = runTest {
        val state = ShortsState(quotes = listOf(quote), currentIdx = 0)
        every { shareManager.buildDeepLink(any()) } returns "http://origin"
        coEvery { shortLinkUseCase("http://origin") } returns "http://s"

        viewModel.test(this, state) {
            containerHost.onShareClick()
            expectSideEffect(ShortsSideEffect.ShareQuote("http://s"))
        }
    }

    @Test
    fun onShareClick_shortlinkFails_shouldPostOriginalUrl() = runTest {
        val state = ShortsState(quotes = listOf(quote), currentIdx = 0)
        every { shareManager.buildDeepLink(any()) } returns "http://origin"
        coEvery { shortLinkUseCase("http://origin") } throws Exception("fail")

        viewModel.test(this, state) {
            containerHost.onShareClick()
            expectSideEffect(ShortsSideEffect.ShareQuote("http://origin"))
        }
    }

    @Test
    fun onPageChanged_validPage_shouldUpdateIdxAndIncreaseViewCount() = runTest {
        val list = listOf(
            quote,
            quote.copy(quoteId = 2L),
            quote.copy(quoteId = 3L)
        )
        // 내부에서 refresh()가 불릴 수 있음. UseCase mockk가 반드시 필요
        coEvery { getQuotesUseCase(any(), any()) } returns emptyList()

        viewModel.test(this, ShortsState(quotes = list)) {
            containerHost.onPageChanged(2)
            val state = awaitState()
            assertEquals(2, state.currentIdx)
            coVerify { updateQuoteViewCountUseCase(3L) }
        }
    }

    @Test
    fun loadQuote_success_shouldUpdateStateAndClearError() = runTest {
        coEvery { getQuoteByIdUseCase(1L) } returns quote
        coEvery { getQuotesUseCase(any(), any()) } returns emptyList()
        viewModel.test(this) {
            containerHost.loadQuote(1L)
            val state = awaitState()
            assertEquals(listOf(quote), state.quotes)
            assertEquals(false, state.isLoading)
            assertEquals(null, state.error)
        }
    }

    @Test
    fun loadQuote_failure_shouldSetError() = runTest {
        coEvery { getQuoteByIdUseCase(2L) } throws Exception("fail!")
        viewModel.test(this) {
            containerHost.loadQuote(2L)
            val state = awaitState()
            assertEquals(false, state.isLoading)
            assertEquals("fail!", state.error)
        }
    }
}
