package com.ssafy.glim.feature.bookdetail

import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.domain.model.Book
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
            }
            .onFailure {
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
}
