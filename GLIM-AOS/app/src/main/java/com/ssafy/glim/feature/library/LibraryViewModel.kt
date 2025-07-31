package com.ssafy.glim.feature.library

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.QuoteSummary
import com.ssafy.glim.core.domain.usecase.book.SearchBooksUseCase
import com.ssafy.glim.core.domain.usecase.quote.SearchQuotesUseCase
import com.ssafy.glim.core.domain.usecase.search.DeleteRecentSearchQueryUseCase
import com.ssafy.glim.core.domain.usecase.search.GetPopularSearchQueriesUseCase
import com.ssafy.glim.core.domain.usecase.search.GetRecentSearchQueriesUseCase
import com.ssafy.glim.core.domain.usecase.search.SaveRecentSearchQueryUseCase
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.library.component.SearchTab
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
@Inject
constructor(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val searchQuotesUseCase: SearchQuotesUseCase,
    private val getPopularSearchQueriesUseCase: GetPopularSearchQueriesUseCase,
    private val getRecentSearchQueriesUseCase: GetRecentSearchQueriesUseCase,
    private val saveRecentSearchQueryUseCase: SaveRecentSearchQueryUseCase,
    private val deleteRecentSearchQueryUseCase: DeleteRecentSearchQueryUseCase,
    private val navigator: Navigator,
) : ViewModel(), ContainerHost<LibraryState, LibrarySideEffect> {
    override val container: Container<LibraryState, LibrarySideEffect> = container(LibraryState())

    init {
        initialize()
    }

    private fun initialize() =
        intent {
            loadPopularSearchItems()
            loadRecentSearchItems()
        }

    // 뒤로가기 처리
    fun onBackPressed() =
        intent {
            when (state.searchMode) {
                SearchMode.RECENT -> {
                    reduce {
                        state.copy(
                            searchMode = SearchMode.POPULAR,
                            searchQuery = "",
                            currentQuery = TextFieldValue(""),
                        )
                    }
                }

                SearchMode.RESULT -> {
                    reduce {
                        state.copy(
                            searchMode = SearchMode.RECENT,
                            searchQuery = "",
                            currentQuery = TextFieldValue(""),
                        )
                    }
                }

                SearchMode.POPULAR -> {
                    postSideEffect(LibrarySideEffect.NavigateBack)
                }
            }
        }

    // 검색어 입력 처리
    fun onSearchQueryChanged(query: TextFieldValue) =
        intent {
            reduce { state.copy(currentQuery = query) }
        }

    // 검색 실행
    fun onSearchExecuted() =
        intent {
            Log.d("LibraryViewModel", "Search executed with query: ${state.currentQuery}")
            val query = state.currentQuery.text.trim()
            if (query.isNotEmpty()) {
                performSearch(query)
                reduce {
                    state.copy(
                        searchQuery = query,
                        searchMode = SearchMode.RESULT,
                    )
                }
                saveRecentSearchQueryUseCase(query)
            }
        }

    fun loadMoreBooks() = intent {
        Log.d("LibraryViewModel", "Loading more books for query: ${state.searchQuery}, page: ${state.bookCurrentPage + 1}")
        reduce {
            state.copy(
                bookCurrentPage = state.bookCurrentPage + 1,
                isRefreshing = true
            )
        }
        runCatching { searchBooksUseCase(state.searchQuery, state.bookCurrentPage, state.selectedFilter.name) }
            .onSuccess {
                reduce {
                    state.copy(
                        searchBooks = state.searchBooks + it,
                        isRefreshing = false,
                        error = null,
                    )
                }
            }
            .onFailure {
                Log.d("LibraryViewModel", "Error searching books: ${it.message}")
                postSideEffect(LibrarySideEffect.ShowToast("검색 중 오류가 발생했습니다."))
            }
    }

    fun loadMoreQuotes() = intent {
        Log.d("LibraryViewModel", "Loading more quotes for query: ${state.searchQuery}, page: ${state.quoteCurrentPage + 1}")
        reduce {
            state.copy(
                quoteCurrentPage = state.quoteCurrentPage + 1,
                isRefreshing = true
            )
        }
        runCatching { searchQuotesUseCase(state.searchQuery, state.quoteCurrentPage) }
            .onSuccess { result ->
                reduce {
                    state.copy(
                        searchQuotes = state.searchQuotes + result.quoteSummaries,
                        isRefreshing = false,
                        error = null,
                    )
                }
            }
            .onFailure {
                Log.d("LibraryViewModel", "Error searching quotes: ${it.message}")
                postSideEffect(LibrarySideEffect.ShowToast("검색 중 오류가 발생했습니다."))
                reduce {
                    state.copy(isRefreshing = false)
                }
            }
    }

    // 인기 검색어 항목 클릭
    fun onPopularSearchItemClicked(query: String) =
        intent {
            reduce { state.copy(searchQuery = query, currentQuery = TextFieldValue(query)) }
            performSearch(query)
            reduce {
                state.copy(
                    searchMode = SearchMode.RESULT,
                )
            }
            saveRecentSearchQueryUseCase(query)
        }

    // 최근 검색어 항목 클릭
    fun onRecentSearchItemClicked(query: String) =
        intent {
            reduce { state.copy(searchQuery = query, currentQuery = TextFieldValue(query)) }
            performSearch(query)
            reduce {
                state.copy(
                    searchMode = SearchMode.RESULT,
                )
            }
            saveRecentSearchQueryUseCase(query)
        }

    // 최근 검색어 삭제 - 수정된 버전
    fun onRecentSearchItemDelete(searchQuery: String) =
        intent {
            reduce {
                state.copy(
                    recentSearchItems =
                    state.recentSearchItems.filter {
                        it != searchQuery
                    },
                    error = null,
                )
            }
            deleteRecentSearchQueryUseCase(searchQuery)
        }

    // 책 아이템 클릭
    fun onBookClicked(book: Book) =
        intent {
            navigator.navigate(Route.BookDetail(isbn = book.isbn))
        }

    // 글귀 아이템 클릭
    fun onQuoteClicked(quote: QuoteSummary) =
        intent {
            navigator.navigate(BottomTabRoute.Reels(quote.quoteId))
        }

    fun onSelectTab(tab: SearchTab) = intent {
        reduce {
            state.copy(selectedTab = tab)
        }
    }
    fun onSelectFilter(filter: SearchFilter) = intent {
        reduce {
            state.copy(selectedFilter = filter)
        }

        performSearch(state.currentQuery.text, filter.name)
    }

    // 인기 검색어 로드
    private fun loadPopularSearchItems() =
        intent {
            reduce { state.copy(isLoading = true) }
            runCatching { getPopularSearchQueriesUseCase() }
                .onSuccess {
                    reduce {
                        state.copy(
                            popularSearchItems = it,
                            isLoading = false,
                            error = null,
                        )
                    }
                    Log.d("LibraryViewModel", "Popular search items loaded: $it")
                }
                .onFailure {
                    Log.d("LibraryViewModel", "Error loading popular search items: ${it.message}")
                }
        }

    // 최근 검색어 로드
    private fun loadRecentSearchItems() =
        intent {
            getRecentSearchQueriesUseCase().collect { items ->
                reduce {
                    state.copy(
                        recentSearchItems = items,
                        error = null,
                    )
                }
            }
        }

    // 검색 수행
    private fun performSearch(query: String, searchQueryType: String = "KEYWORD") =
        intent {
            reduce { state.copy(isLoading = true) }
            runCatching { searchBooksUseCase(query, state.bookCurrentPage, searchQueryType) }
                .onSuccess {
                    reduce {
                        state.copy(
                            searchBooks = it,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
                .onFailure {
                    Log.d("LibraryViewModel", "Error searching books: ${it.message}")
                    postSideEffect(LibrarySideEffect.ShowToast("검색 중 오류가 발생했습니다."))
                }

            runCatching { searchQuotesUseCase(query) }
                .onSuccess {
                    reduce {
                        state.copy(
                            searchQuotes = it.quoteSummaries,
                            totalResults = it.totalResults,
                            isLoading = false,
                            error = null
                        )
                    }
                    Log.d("LibraryViewModel like", "${it.quoteSummaries.map{it.isLiked}}")
                }

            reduce { state.copy(isLoading = false) }
        }

    fun updateSearchMode(searchMode: SearchMode) =
        intent {
            reduce { state.copy(searchMode = searchMode) }
        }
}
