package com.ssafy.glim.feature.library

import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.model.SearchItem
import com.ssafy.glim.feature.library.component.SearchTab


enum class SearchMode {
    POPULAR,
    RECENT,
    RESULT
}

data class LibraryState(
    val searchQuery: String = "",
    val currentQuery: String = "",
    val searchMode: SearchMode = SearchMode.POPULAR,
    val selectedTab: SearchTab = SearchTab.BOOKS,
    val isLoading: Boolean = false,
    val error: String? = null,
    val popularSearchItems: List<SearchItem> = emptyList(),
    val recentSearchItems: List<String> = emptyList(),
    val searchBooks: List<Book> = emptyList(),
    val searchQuotes: List<Quote> = emptyList(),
)

sealed class LibrarySideEffect {
    data object NavigateBack : LibrarySideEffect()
    data class ShowToast(val message: String) : LibrarySideEffect()
}