package com.ssafy.glim.feature.lock

import com.ssafy.glim.core.domain.model.Glim
import java.time.LocalDateTime


data class LockUiState(
    val isLoading: Boolean = true,
    val time: LocalDateTime = LocalDateTime.now(),
    val quotes: List<Glim> = emptyList(),
    val currentIndex: Int = 0,
    val page: Int = 0,
    val size: Int = 20,
    val isComplete: Boolean = false
)
sealed interface LockSideEffect {
    object Unlock : LockSideEffect
    object ShowSaveToast : LockSideEffect
    object ShowFavoriteToast : LockSideEffect
    object NavigateQuotes : LockSideEffect
    object NavigateBook : LockSideEffect
}