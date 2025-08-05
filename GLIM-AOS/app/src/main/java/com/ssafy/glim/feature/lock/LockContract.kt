package com.ssafy.glim.feature.lock

import androidx.annotation.StringRes
import com.ssafy.glim.core.domain.model.Quote
import java.time.LocalDateTime

data class LockUiState(
    val isLoading: Boolean = true,
    val time: LocalDateTime = LocalDateTime.now(),
    val quotes: List<Quote> = emptyList(),
    val currentIndex: Int = 0,
    val page: Int = 0,
    val size: Int = 20,
    val isComplete: Boolean = false,
){
    val currentQuote: Quote?
        get() =
            if (currentIndex >= 0 && currentIndex < quotes.size) {
                quotes[currentIndex]
            } else {
                null
            }
}

sealed interface LockSideEffect {
    data object Unlock : LockSideEffect

    data class ShowToast(
        @StringRes val messageRes: Int,
    ) : LockSideEffect

    data class SaveImage(val imageUrl: String) : LockSideEffect

    data class NavigateQuotes(val quoteId: Long) : LockSideEffect

    data class NavigateBook(val bookId: Long) : LockSideEffect

}
