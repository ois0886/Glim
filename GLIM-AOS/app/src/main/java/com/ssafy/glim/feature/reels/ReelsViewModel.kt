package com.ssafy.quote.feature.reels

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
                val currentQuote = state.quotes[page]
                reduce {
                    state.copy(
                        currentPage = page,
                        currentQuoteId = currentQuote.quoteId,
                    )
                }
            }
        }

    fun onShareClick() =
        intent {
            state.currentQuote?.let {
                postSideEffect(ReelsSideEffect.ShareQuote(it))
            }
        }

    fun onCaptureClick(fileName: String) =
        intent {
            try {
                // 캡처 로직은 외부에서 처리하고 결과만 받음
                postSideEffect(ReelsSideEffect.CaptureSuccess(fileName))
            } catch (e: Exception) {
                postSideEffect(ReelsSideEffect.CaptureError("캡처에 실패했습니다: ${e.message}"))
            }
        }

    fun refresh() =
        intent {
            reduce {
                state.copy(
                    quotes = emptyList(),
                    currentPage = 0,
                    currentQuoteId = -1,
                    hasMoreData = true,
                )
            }
            loadInitialQuotes()
        }

    fun onBookInfoClick(bookId: Long) =
        intent {
            navigator.navigate(Route.BookDetail(bookId))
        }

    private fun loadInitialQuotes() =
        intent {
            reduce { state.copy(isLoading = true, error = null) }

            runCatching { getQuotesUseCase(page = state.currentPage + 1) }
                .onSuccess {
                    val newQuotes = it.filter { quote -> quote.quoteId != state.currentQuoteId }
                    if (newQuotes.isNotEmpty()) {
                        reduce {
                            state.copy(
                                quotes = state.quotes + newQuotes,
                                currentPage = state.currentPage + 1,
                                hasMoreData = newQuotes.size >= 10, // Assuming page size is 10
                                isLoading = false,
                                error = null
                            )
                        }
                    } else {
                        reduce { state.copy(hasMoreData = false, isLoading = false) }
                    }

                    // Update view count for the first quote in the list
                    if (newQuotes.isNotEmpty()) {
                        updateQuoteViewCountUseCase(newQuotes.first().quoteId)
                    }
                }
                .onFailure {
                    reduce { state.copy(isLoading = false, error = it.message) }
                }
        }
}
