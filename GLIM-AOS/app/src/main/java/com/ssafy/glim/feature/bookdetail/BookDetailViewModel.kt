package com.ssafy.glim.feature.bookdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.domain.usecase.book.GetBookDetailUseCase
import com.ssafy.glim.core.domain.usecase.book.UpdateBookViewCountUseCase
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.feature.main.MainTab
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val updateBookViewCountUseCase: UpdateBookViewCountUseCase,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<BookDetailState, BookDetailSideEffect> {

    override val container: Container<BookDetailState, BookDetailSideEffect> = container(BookDetailState())

    fun initBook(bookId: Long) = intent {
        runCatching { getBookDetailUseCase(bookId) }
            .onSuccess {
                reduce {
                    state.copy(
                        bookDetail = it,
                        isLoading = false
                    )
                }
                increaseViewCount(bookId)
            }
            .onFailure {
                Log.d("BookDetailViewModel", "Failed to load book details: ${it.message}")
                postSideEffect(BookDetailSideEffect.ShowToast("책 정보를 불러오는데 실패했습니다."))
            }
    }

    fun onClickQuote(quoteId: Long) = intent {
        navigator.navigate(MainTab.REELS.route)
    }

    fun openUrl() = intent {
        postSideEffect(BookDetailSideEffect.OpenUrl(state.bookDetail.link))
    }

    fun toggleBookDescriptionExpanded() = intent {
        reduce {
            state.copy(
                isDescriptionExpanded = !state.isDescriptionExpanded
            )
        }
    }

    fun toggleAuthorDescriptionExpanded() = intent {
        reduce {
            state.copy(
                isAuthorDescriptionExpanded = !state.isAuthorDescriptionExpanded
            )
        }
    }

    private fun increaseViewCount(bookId: Long) = intent {
        runCatching { updateBookViewCountUseCase(bookId) }
            .onSuccess {
                // 성공적으로 조회수 증가
            }
            .onFailure {
                postSideEffect(BookDetailSideEffect.ShowToast("조회수 증가에 실패했습니다."))
            }
    }
}
