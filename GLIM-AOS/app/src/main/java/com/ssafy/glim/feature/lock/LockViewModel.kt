package com.ssafy.glim.feature.lock

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.glim.BuildConfig
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.usecase.quote.GetQuotesUseCase
import com.ssafy.glim.core.domain.usecase.quote.LikeQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.UnLikeQuoteUseCase
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
    private val likeQuoteUseCase: LikeQuoteUseCase,
    private val unLikeQuoteUseCase: UnLikeQuoteUseCase
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
        val lastIndex = state.quotes.lastIndex
        val nextIdx = (state.currentIndex + 1).coerceAtMost(lastIndex)

        if (nextIdx >= state.quotes.size - 5) loadQuotes()
        if (nextIdx != state.currentIndex) {
            reduce { state.copy(currentIndex = nextIdx) }
        }
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
            state.quotes.getOrNull(state.currentIndex)?.let { quote ->
                val url = "${BuildConfig.BASE_URL}/images/${quote.quoteImageName}"
                postSideEffect(LockSideEffect.SaveImage(url))
            }
        }

    fun toggleLike() =
        intent {
            val currentQuote = state.currentQuote
            if (currentQuote == null) {
                postSideEffect(LockSideEffect.ShowToast(R.string.like_error_message))
                return@intent
            }
            val updatedQuotes =
                state.quotes.map { quote ->
                    if (quote.quoteId == currentQuote.quoteId) {
                        val newIsLike = !quote.isLike
                        quote.copy(
                            isLike = newIsLike,
                            likes = if (newIsLike) quote.likes + 1 else quote.likes - 1,
                        )
                    } else {
                        quote
                    }
                }

            viewModelScope.launch {
                runCatching {
                    if (currentQuote.isLike) {
                        unLikeQuoteUseCase(currentQuote.quoteId)
                    } else {
                        likeQuoteUseCase(currentQuote.quoteId)
                    }
                }.onFailure {
                    Log.d("ShortsViewModel", "${it.message}")
                    postSideEffect(LockSideEffect.ShowToast(R.string.like_error_message))
                }
            }

            reduce { state.copy(quotes = updatedQuotes) }
        }

    fun openCamera() = intent {
        postSideEffect(LockSideEffect.NavigateCamera)
    }

    fun viewQuote() =
        intent {
            postSideEffect(LockSideEffect.NavigateQuotes(state.quotes[state.currentIndex].quoteId))
        }

    private fun loadQuotes() = intent {
        runCatching {
            getQuotesUseCase(state.page, state.size)
        }.onSuccess { newQuotes ->
            reduce {
                val updatedQuotes = state.quotes + newQuotes
                state.copy(
                    quotes = updatedQuotes,
                    page = state.page + 1
                )
            }
        }
    }
}
