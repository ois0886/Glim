package com.ssafy.glim.feature.library

import androidx.compose.ui.text.input.TextFieldValue
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.QuoteSummary
import com.ssafy.glim.core.domain.model.SearchItem
import com.ssafy.glim.feature.library.component.SearchTab

enum class SearchMode {
    POPULAR,
    RECENT,
    RESULT,
}

enum class SearchFilter(val filterName: String) {
    KEYWORD("전체"),
    TITLE("제목"),
    AUTHOR("작가"),
    PUBLISHER("출판사")
}

data class LibraryState(
    val searchQuery: String = "",
    val currentQuery: TextFieldValue = TextFieldValue(""),
    val searchMode: SearchMode = SearchMode.POPULAR,
    val selectedTab: SearchTab = SearchTab.BOOKS,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val bookCurrentPage: Int = 0,
    val quoteCurrentPage: Int = 0,
    val error: String? = null,
    val popularSearchItems: List<SearchItem> = emptyList(),
    val recentSearchItems: List<String> = emptyList(),
    val searchBooks: List<Book> = emptyList(),
    val searchQuotes: List<QuoteSummary> = emptyList(),
    val selectedFilter: SearchFilter = SearchFilter.KEYWORD
)

sealed class LibrarySideEffect {
    data object NavigateBack : LibrarySideEffect()

    data class ShowToast(val message: String) : LibrarySideEffect()
}
