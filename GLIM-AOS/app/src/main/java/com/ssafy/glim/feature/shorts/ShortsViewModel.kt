package com.ssafy.glim.feature.shorts

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.glim.core.common.extensions.toUri
import com.ssafy.glim.core.domain.usecase.quote.GetQuoteByIdUseCase
import com.ssafy.glim.core.domain.usecase.quote.GetQuotesUseCase
import com.ssafy.glim.core.domain.usecase.quote.LikeQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.UnLikeQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.UpdateQuoteViewCountUseCase
import com.ssafy.glim.core.domain.usecase.shortlink.ShortenUrlUseCase
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.core.util.ShareManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ShortsViewModel
@Inject
constructor(
    private val getQuotesUseCase: GetQuotesUseCase,
    private val updateQuoteViewCountUseCase: UpdateQuoteViewCountUseCase,
    private val getQuoteByIdUseCase: GetQuoteByIdUseCase,
    private val likeQuoteUseCase: LikeQuoteUseCase,
    private val unLikeQuoteUseCase: UnLikeQuoteUseCase,
    private val shortLinkUseCase: ShortenUrlUseCase,
    private val shareManager: ShareManager,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<ShortsState, ShortsSideEffect> {
    override val container: Container<ShortsState, ShortsSideEffect> = container(ShortsState())

    companion object {
        private const val SIZE = 10
    }

    fun toggleLike() =
        intent {
            val currentQuote = state.currentQuote
            if (currentQuote == null) {
                postSideEffect(ShortsSideEffect.ShowToast("오류 발생"))
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
                    postSideEffect(ShortsSideEffect.ShowToast("좋아요 오류 발생"))
                }
            }

            reduce { state.copy(quotes = updatedQuotes) }
        }

    fun onPageChanged(page: Int) =
        intent {
            Log.d("ShortsViewModel", "$page / ${state.quotes.size} 페이지로 변경됨")
            // 페이지가 변경될 때 currentQuote.quoteId도 업데이트
            if (page >= 0 && page < state.quotes.size) {
                reduce {
                    state.copy(
                        currentIdx = page,
                    )
                }
                Log.d("ShortsViewModel", "현재 Quote Idx: ${state.currentIdx} / ${state.quotes.size}")
                if (state.currentIdx >= state.quotes.size - 3) {
                    refresh()
                }
                val currentQuote = state.currentQuote
                if (currentQuote == null) {
                    postSideEffect(ShortsSideEffect.ShowToast("오류 발생"))
                    return@intent
                }
                runCatching { updateQuoteViewCountUseCase(currentQuote.quoteId) }
                    .onSuccess {
                        Log.d("ShortsViewModel", "Quote view count updated successfully")
                    }
                    .onFailure {
                        Log.d("ShortsViewModel", "Failed to update quote view count: ${it.message}")
                    }
            }
        }

    fun onShareClick() = intent {
        val originalUrl = shareManager.buildDeepLink(state.currentQuote ?: return@intent)
        runCatching { shortLinkUseCase(originalUrl) }
            .onSuccess {
                postSideEffect(ShortsSideEffect.ShareQuote(it))
            }
            .onFailure {
                Log.d("ShortsViewModel", "Shorten URL failed: ${it.message}")
                postSideEffect(ShortsSideEffect.ShareQuote(originalUrl))
            }
    }

    fun onInstagramShareClick(context: Context) = intent {
        val currentQuote = state.currentQuote ?: return@intent
        val uri = currentQuote.quoteImageUrl.toUri(context)
        postSideEffect(ShortsSideEffect.ShareQuoteInstagram(uri ?: return@intent))
    }

    fun loadQuote(quoteId: Long) = intent {
        runCatching { getQuoteByIdUseCase(quoteId) }
            .onSuccess {
                Log.d("ShortsViewModel", "Loaded quote: $it")
                reduce {
                    state.copy(
                        quotes = listOf(it),
                        isLoading = false,
                        error = null
                    )
                }
                updateQuoteViewCountUseCase(quoteId)
                refresh()
            }
            .onFailure {
                Log.d("ShortsViewModel", "Failed to load quote: ${it.message}")
                reduce {
                    state.copy(
                        isLoading = false,
                        error = it.message
                    )
                }
            }
    }

    fun refresh() =
        intent {
            runCatching { getQuotesUseCase(page = state.currentPage + 1, size = SIZE) }
                .onSuccess {
                    if (it.isNotEmpty()) {
                        reduce {
                            state.copy(
                                quotes = state.quotes + it,
                                currentPage = state.currentPage + 1,
                                isLoading = false,
                                error = null
                            )
                        }
                        Log.d("ShortsViewModel", "Loaded ${it.size} new quotes")
                        Log.d("ShortsViewModel", "Total quotes: ${state.quotes.size}")
                    }
                }
                .onFailure {
                    Log.d("ShortsViewModel", "Loaded failed: ${it.message}")
                    reduce { state.copy(isLoading = false, error = it.message) }
                }
        }

    fun onBookInfoClick(bookId: Long) =
        intent {
            navigator.navigate(Route.BookDetail(bookId = bookId))
        }
}
