package com.ssafy.glim.feature.reels

import com.ssafy.glim.core.domain.model.Quote

// ReelsContract.kt
data class ReelsState(
    val quotes: List<Quote> = emptyList(),
    val currentIdx: Int = 0,
    val currentPage: Int = -1,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val currentQuote: Quote?
        get() =
            if (currentIdx >= 0 && currentIdx < quotes.size) {
                quotes[currentIdx]
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
