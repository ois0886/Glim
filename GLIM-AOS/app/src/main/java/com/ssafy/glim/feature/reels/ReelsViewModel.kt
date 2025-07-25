package com.ssafy.glim.feature.reels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.glim.core.domain.usecase.quote.GetQuotesUseCase
import com.ssafy.glim.core.domain.usecase.quote.UpdateQuoteViewCountUseCase
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel
@Inject
constructor(
    private val getQuotesUseCase: GetQuotesUseCase,
    private val updateQuoteViewCountUseCase: UpdateQuoteViewCountUseCase,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<ReelsState, ReelsSideEffect> {
    override val container: Container<ReelsState, ReelsSideEffect> = container(ReelsState())

    init {
        refresh()
    }

    companion object {
        private const val SIZE = 10 // 한 번에 가져올 인용구의 개수
    }

    fun toggleLike() =
        intent {
            val updatedQuotes =
                state.quotes.map { quote ->
                    if (quote.quoteId == state.currentQuoteId) {
                        val newIsLike = !quote.isLike
                        quote.copy(
                            isLike = newIsLike,
                            likes = if (newIsLike) quote.likes + 1 else quote.likes - 1,
                        )
                    } else {
                        quote
                    }
                }

            reduce { state.copy(quotes = updatedQuotes) }

            // 서버에 실제 업데이트
            viewModelScope.launch {
                // TODO: API 호출
                // toggleLikeUseCase(state.currentQuoteId)
            }
        }

    fun onPageChanged(page: Int) =
        intent {
            // 페이지가 변경될 때 currentQuoteId도 업데이트
            if (page >= 0 && page < state.quotes.size) {
                reduce {
                    state.copy(
                        currentIdx = page,
                        currentQuoteId = state.currentQuote?.quoteId ?: -1,
                    )
                }
                if(state.currentIdx >= state.quotes.size - 3) {
                    // 마지막 페이지에 도달했을 때 새로운 인용구를 가져옴
                    refresh()
                }
//                updateQuoteViewCountUseCase(state.currentQuoteId)
            }
        }

    fun onShareClick() =
        intent {
            state.currentQuote?.let {
                postSideEffect(ReelsSideEffect.ShareQuote(it))
            }
        }

    private fun refresh() =
        intent {
            runCatching { getQuotesUseCase(page = state.currentPage + 1, size = SIZE) }
                .onSuccess {
                    val newQuotes = it.filter { quote -> quote.quoteId != state.currentQuoteId }
                    if (newQuotes.isNotEmpty()) {
                        reduce {
                            state.copy(
                                quotes = state.quotes + newQuotes,
                                currentPage = state.currentPage + 1,
                                isLoading = false,
                                error = null
                            )
                        }
                        Log.d("ReelsViewModel", "Loaded ${newQuotes.size} new quotes")
                        Log.d("ReelsViewModel", "Total quotes: ${state.quotes.size}")
                    }
                }
                .onFailure {
                    Log.d("ReelsViewModel", "Loaded failed: ${it.message}")
                    reduce { state.copy(isLoading = false, error = it.message) }
                }
        }

    fun onBookInfoClick(bookId: Long) =
        intent {
            navigator.navigate(Route.BookDetail(bookId))
        }

}
