package com.ssafy.glim.feature.library

import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.usecase.book.SearchBooksUseCase
import com.ssafy.glim.core.domain.usecase.quote.SearchQuotesUseCase
import com.ssafy.glim.core.domain.usecase.search.DeleteRecentSearchQueryUseCase
import com.ssafy.glim.core.domain.usecase.search.GetPopularSearchQueriesUseCase
import com.ssafy.glim.core.domain.usecase.search.GetRecentSearchQueriesUseCase
import com.ssafy.glim.core.domain.usecase.search.SaveRecentSearchQueryUseCase
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
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
                            currentQuery = "",
                        )
                    }
                }

                SearchMode.RESULT -> {
                    reduce {
                        state.copy(
                            searchMode = SearchMode.RECENT,
                            searchQuery = "",
                            currentQuery = "",
                        )
                    }
                }

                SearchMode.POPULAR -> {
                    postSideEffect(LibrarySideEffect.NavigateBack)
                }
            }
        }

    // 검색어 입력 처리
    fun onSearchQueryChanged(query: String) =
        intent {
            reduce { state.copy(currentQuery = query) }
        }

    // 검색 실행
    fun onSearchExecuted() =
        intent {
            val query = state.currentQuery.trim()
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

    // 인기 검색어 항목 클릭
    fun onPopularSearchItemClicked(query: String) =
        intent {
            reduce { state.copy(searchQuery = query, currentQuery = query) }
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
            reduce { state.copy(searchQuery = query, currentQuery = query) }
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
            navigator.navigate(Route.BookDetail(bookId = book.itemId))
        }

    // 글귀 아이템 클릭
    fun onQuoteClicked(quote: Quote) =
        intent {
            navigator.navigate(BottomTabRoute.Reels)
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
    private fun performSearch(query: String) =
        intent {
            reduce { state.copy(isLoading = true) }
            try {
                loadRecentSearchItems()
                // 책 검색
                runCatching { searchBooksUseCase(query) }
                    .onSuccess {
                        reduce {
                            state.copy(
                                searchBooks = it,
                                isLoading = false,
                                error = null,
                            )
                        }
                    }

                // 글귀 검색
                runCatching { searchQuotesUseCase(query) }
                    .onSuccess {
                        reduce {
                            state.copy(
                                searchQuotes = it,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                reduce {
                    state.copy(
                        isLoading = false,
                        error = "검색 중 오류가 발생했습니다."
                    )
                }
                postSideEffect(LibrarySideEffect.ShowToast("검색 중 오류가 발생했습니다."))
            }
        }

    fun updateSearchMode(searchMode: SearchMode) =
        intent {
            reduce { state.copy(searchMode = searchMode) }
        }
}
