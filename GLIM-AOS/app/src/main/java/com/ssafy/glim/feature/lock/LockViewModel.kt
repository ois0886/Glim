package com.ssafy.glim.feature.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.usecase.quote.GetQuotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class LockViewModel
@Inject
constructor(
    private val getQuotesUseCase: GetQuotesUseCase,
) : ViewModel(), ContainerHost<LockUiState, LockSideEffect> {
    override val container =
        container<LockUiState, LockSideEffect>(
            initialState = LockUiState(),
        ) {
        }

    init {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                tick()
            }
        }
        loadQuotes()
    }

    fun tick() =
        intent {
            reduce { state.copy(time = LocalDateTime.now()) }
        }

    fun nextQuote() = intent {
        val nextIdx = state.currentIndex + 1
        // 뒤에서 5개 남았을 때 추가 로드
        if (nextIdx >= state.quotes.size - 5) loadQuotes()
        reduce { state.copy(currentIndex = nextIdx) }
    }
    fun prevQuote() = intent {
        var prevIdx = state.currentIndex - 1
        if (prevIdx < 0) {
            prevIdx = 0
        }
        reduce { state.copy(currentIndex = prevIdx) }
    }
    fun unlockMain() = intent {
        reduce { state.copy(isComplete = true) }
        postSideEffect(LockSideEffect.Unlock)
    }

    fun saveGlim() =
        intent {
            postSideEffect(LockSideEffect.ShowToast(R.string.saved))
        }

    fun favoriteGlim() =
        intent {
            postSideEffect(LockSideEffect.ShowToast(R.string.i_love_it))
        }

    fun viewBook() =
        intent {
            val currentQuote = state.quotes[state.currentIndex]
            postSideEffect(LockSideEffect.NavigateBook(currentQuote.bookId))
        }

    fun viewQuote() =
        intent {
            postSideEffect(LockSideEffect.NavigateQuotes)
        }

    private fun loadQuotes() =
        intent {
            val page = state.page
            runCatching { getQuotesUseCase(page, state.size) }
                .onSuccess {
                    reduce {
                        state.copy(
                            quotes = state.quotes + it,
                            page = page + 1,
                        )
                    }
                }
        }
}
