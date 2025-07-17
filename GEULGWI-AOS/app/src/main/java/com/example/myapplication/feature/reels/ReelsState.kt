package com.example.myapplication.feature.reels

import com.example.myapplication.core.domain.model.Glim

// ReelsState.kt
data class ReelsState(
    val glims: List<Glim> = emptyList(),
    val currentGlimId: Long = -1,
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasMoreData: Boolean = true,
) {
    val currentGlim: Glim?
        get() =
            if (currentPage >= 0 && currentPage < glims.size) {
                glims[currentPage]
            } else {
                null
            }
}

// ReelsSideEffect.kt
sealed class ReelsSideEffect {
    data class ShowToast(val message: String) : ReelsSideEffect()

    data class ShareGlim(val glim: Glim) : ReelsSideEffect()

    data class ShowMoreOptions(val glim: Glim) : ReelsSideEffect()

    data class CaptureSuccess(val fileName: String) : ReelsSideEffect()

    data class CaptureError(val error: String) : ReelsSideEffect()
}
