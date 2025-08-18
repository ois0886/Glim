package com.ssafy.glim.feature.shorts

import android.net.Uri
import com.ssafy.glim.core.domain.model.Quote

// ShortsContract.kt
data class ShortsState(
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

// ShortsSideEffect.kt
sealed class ShortsSideEffect {
    data class ShowToast(val message: String) : ShortsSideEffect()

    data class ShareQuote(val url: String) : ShortsSideEffect()

    data class ShareQuoteInstagram(val imageUri: Uri) : ShortsSideEffect()

    data class ShowMoreOptions(val quote: Quote) : ShortsSideEffect()

    data class CaptureSuccess(val fileName: String) : ShortsSideEffect()

    data class CaptureError(val error: String) : ShortsSideEffect()
}
