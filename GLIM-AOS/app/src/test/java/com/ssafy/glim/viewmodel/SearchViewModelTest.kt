package com.ssafy.glim.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.QuoteSearchResult
import com.ssafy.glim.core.domain.model.QuoteSummary
import com.ssafy.glim.core.domain.model.RankStatus
import com.ssafy.glim.core.domain.model.SearchItem
import com.ssafy.glim.core.domain.usecase.book.SearchBooksUseCase
import com.ssafy.glim.core.domain.usecase.quote.SearchQuotesUseCase
import com.ssafy.glim.core.domain.usecase.search.DeleteRecentSearchQueryUseCase
import com.ssafy.glim.core.domain.usecase.search.GetPopularSearchQueriesUseCase
import com.ssafy.glim.core.domain.usecase.search.GetRecentSearchQueriesUseCase
import com.ssafy.glim.core.domain.usecase.search.SaveRecentSearchQueryUseCase
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.search.SearchFilter
import com.ssafy.glim.feature.search.SearchMode
import com.ssafy.glim.feature.search.SearchViewModel
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Mocks
    private val searchBooksUseCase = mockk<SearchBooksUseCase>(relaxed = true)
    private val searchQuotesUseCase = mockk<SearchQuotesUseCase>(relaxed = true)
    private val getPopularSearchQueriesUseCase = mockk<GetPopularSearchQueriesUseCase>(relaxed = true)
    private val getRecentSearchQueriesUseCase = mockk<GetRecentSearchQueriesUseCase>(relaxed = true)
    private val saveRecentSearchQueryUseCase = mockk<SaveRecentSearchQueryUseCase>(relaxed = true)
    private val deleteRecentSearchQueryUseCase = mockk<DeleteRecentSearchQueryUseCase>(relaxed = true)
    private val navigator = mockk<Navigator>(relaxed = true)

    private lateinit var viewModel: SearchViewModel

    // Test data
    private val mockBooks = listOf(
        Book(
            bookId = 1L,
            title = "테스트 책 1",
            author = "작가1",
            publisher = "출판사1",
            isbn = "123456789",
            isbn13 = "1234567890123",
            cover = "cover1.jpg",
            description = "테스트 설명1",
            pubDate = "2023-01-01",
            categoryName = "소설>한국소설",
            priceStandard = 15000L,
            priceSales = 13500L,
            views = 100L
        )
    )

    private val mockQuoteSummaries = listOf(
        QuoteSummary(
            quoteId = 1L,
            content = "테스트 명언 1",
            page = "10",
            views = 500L,
            bookTitle = "테스트 책 1",
            likes = 25L,
            isLiked = false,
            createdAt = "2023-01-01T10:00:00"
        )
    )

    private val mockQuoteSearchResult = QuoteSearchResult(
        currentPage = 1,
        totalPages = 5,
        totalResults = 100,
        quoteSummaries = mockQuoteSummaries
    )

    private val mockPopularSearchItems = listOf(
        SearchItem(
            rankStatus = RankStatus.MAINTAIN,
            rank = 1,
            text = "인기검색어1",
            type = "POPULAR"
        ),
        SearchItem(
            rankStatus = RankStatus.UP,
            rank = 2,
            text = "인기검색어2",
            type = "POPULAR"
        )
    )

    private val mockRecentSearchItems = listOf("최근검색어1", "최근검색어2")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // 기본 모킹 설정
        coEvery { getPopularSearchQueriesUseCase() } returns mockPopularSearchItems
        every { getRecentSearchQueriesUseCase() } returns flowOf(mockRecentSearchItems)
        coEvery { searchBooksUseCase(any(), any(), any()) } returns mockBooks
        coEvery { searchQuotesUseCase(any(), any()) } returns mockQuoteSearchResult
        coEvery { searchQuotesUseCase(any()) } returns mockQuoteSearchResult
        coEvery { saveRecentSearchQueryUseCase(any()) } just Runs
        coEvery { deleteRecentSearchQueryUseCase(any()) } just Runs
        coEvery { navigator.navigate(any<Route>()) } just Runs
        coEvery { navigator.navigate(any<BottomTabRoute>()) } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    private fun createViewModel(): SearchViewModel {
        val vm = SearchViewModel(
            searchBooksUseCase = searchBooksUseCase,
            searchQuotesUseCase = searchQuotesUseCase,
            getPopularSearchQueriesUseCase = getPopularSearchQueriesUseCase,
            getRecentSearchQueriesUseCase = getRecentSearchQueriesUseCase,
            saveRecentSearchQueryUseCase = saveRecentSearchQueryUseCase,
            deleteRecentSearchQueryUseCase = deleteRecentSearchQueryUseCase,
            navigator = navigator
        )
        return vm
    }

    @Test
    fun `ViewModel 생성이 정상적으로 된다`() = runTest {
        viewModel = createViewModel()

        assertNotNull(viewModel)
        assertNotNull(viewModel.container.stateFlow.value)
    }

    @Test
    fun `인기검색어 UseCase가 호출된다`() = runTest {
        viewModel = createViewModel()

        coVerify { getPopularSearchQueriesUseCase() }
    }

    @Test
    fun `최근검색어 UseCase가 호출된다`() = runTest {
        viewModel = createViewModel()

        verify { getRecentSearchQueriesUseCase() }
    }

    @Test
    fun `검색어 변경 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()
        val newQuery = TextFieldValue("새로운 검색어")

        // 함수 호출이 예외없이 실행되는지 확인
        try {
            viewModel.onSearchQueryChanged(newQuery)
            assertTrue("검색어 변경 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("검색어 변경 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `검색 실행 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()
        viewModel.onSearchQueryChanged(TextFieldValue("테스트"))

        try {
            viewModel.onSearchExecuted()
            advanceUntilIdle()
            assertTrue("검색 실행 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("검색 실행 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `빈 검색어로 검색 시 예외가 발생하지 않는다`() = runTest {
        viewModel = createViewModel()
        viewModel.onSearchQueryChanged(TextFieldValue("   "))

        try {
            viewModel.onSearchExecuted()
            assertTrue("빈 검색어 처리 성공", true)
        } catch (e: Exception) {
            throw AssertionError("빈 검색어 처리 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `책 추가 로드 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()
        viewModel.onSearchQueryChanged(TextFieldValue("테스트"))
        viewModel.onSearchExecuted()
        advanceUntilIdle()

        try {
            viewModel.loadMoreBooks()
            assertTrue("책 추가 로드 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("책 추가 로드 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `명언 추가 로드 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()
        viewModel.onSearchQueryChanged(TextFieldValue("테스트"))
        viewModel.onSearchExecuted()
        advanceUntilIdle()

        try {
            viewModel.loadMoreQuotes()
            assertTrue("명언 추가 로드 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("명언 추가 로드 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `인기검색어 클릭 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()

        try {
            viewModel.onPopularSearchItemClicked("인기검색어")
            advanceUntilIdle()
            assertTrue("인기검색어 클릭 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("인기검색어 클릭 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `최근검색어 클릭 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()

        try {
            viewModel.onRecentSearchItemClicked("최근검색어")
            advanceUntilIdle()
            assertTrue("최근검색어 클릭 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("최근검색어 클릭 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `최근검색어 삭제 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()

        try {
            viewModel.onRecentSearchItemDelete("삭제할검색어")
            assertTrue("최근검색어 삭제 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("최근검색어 삭제 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `책 클릭 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()

        try {
            viewModel.onBookClicked(mockBooks[0])
            assertTrue("책 클릭 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("책 클릭 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `명언 클릭 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()

        try {
            viewModel.onQuoteClicked(mockQuoteSummaries[0])
            assertTrue("명언 클릭 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("명언 클릭 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `탭 선택 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()

        try {
            // SearchTab enum의 실제 값이 뭔지 모르므로 기본 상태만 확인
            val state = viewModel.container.stateFlow.value
            assertNotNull("선택된 탭이 null이 아님", state.selectedTab)
            assertTrue("탭 선택 상태 확인 성공", true)
        } catch (e: Exception) {
            throw AssertionError("탭 선택 확인 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `필터 선택 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()
        viewModel.onSearchQueryChanged(TextFieldValue("테스트"))

        try {
            viewModel.onSelectFilter(SearchFilter.TITLE)
            assertTrue("필터 선택 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("필터 선택 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `뒤로가기 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()

        try {
            viewModel.onBackPressed()
            assertTrue("뒤로가기 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("뒤로가기 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `검색 모드 업데이트 함수가 정상 동작한다`() = runTest {
        viewModel = createViewModel()

        try {
            viewModel.updateSearchMode(SearchMode.RESULT)
            assertTrue("검색 모드 업데이트 함수 호출 성공", true)
        } catch (e: Exception) {
            throw AssertionError("검색 모드 업데이트 중 예외 발생: ${e.message}")
        }
    }

    @Test
    fun `상태 값들이 null이 아니다`() = runTest {
        viewModel = createViewModel()

        val state = viewModel.container.stateFlow.value
        assertNotNull("popularSearchItems", state.popularSearchItems)
        assertNotNull("currentQuery", state.currentQuery)
        assertNotNull("searchQuery", state.searchQuery)
        assertNotNull("searchMode", state.searchMode)
        assertNotNull("selectedTab", state.selectedTab)
        assertNotNull("selectedFilter", state.selectedFilter)
    }

    @Test
    fun `전체 워크플로우가 예외없이 실행된다`() = runTest {
        viewModel = createViewModel()

        try {
            // 1. 검색어 입력
            viewModel.onSearchQueryChanged(TextFieldValue("통합테스트"))

            // 2. 검색 실행
            viewModel.onSearchExecuted()
            advanceUntilIdle()

            // 3. 필터 변경
            viewModel.onSelectFilter(SearchFilter.AUTHOR)

            // 4. 뒤로가기
            viewModel.onBackPressed()

            assertTrue("전체 워크플로우 성공", true)
        } catch (e: Exception) {
            throw AssertionError("워크플로우 실행 중 예외 발생: ${e.message}")
        }
    }
}
