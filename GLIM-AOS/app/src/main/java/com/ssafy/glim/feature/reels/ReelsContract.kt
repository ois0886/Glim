package com.ssafy.quote.feature.reels

import com.ssafy.glim.core.domain.model.Quote

// ReelsContract.kt
data class ReelsState(
    val quotes: List<Quote> = emptyList(),
    val currentQuoteId: Long = -1,
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasMoreData: Boolean = true,
) {
    val currentQuote: Quote?
        get() =
            if (currentPage >= 0 && currentPage < quotes.size) {
                quotes[currentPage]
            } else {
                null
            }
}

// ReelsSideEffect.kt
sealed class ReelsSideEffect {
    data class ShowToast(val message: String) : ReelsSideEffect()

    data class ShareQuote(val quote: Quote) : ReelsSideEffect()

    data class ShowMoreOptions(val quote: Quote) : ReelsSideEffect()

    data class CaptureSuccess(val fileName: String) : ReelsSideEffect()

    data class CaptureError(val error: String) : ReelsSideEffect()
}
