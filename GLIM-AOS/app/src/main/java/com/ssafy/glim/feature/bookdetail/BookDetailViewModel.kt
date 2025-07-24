package com.ssafy.glim.feature.bookdetail

import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.domain.model.Book
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
    private val updateBookViewCountUseCase: UpdateBookViewCountUseCase,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<BookDetailState, BookDetailSideEffect> {

    override val container: Container<BookDetailState, BookDetailSideEffect> = container(BookDetailState())

    fun loadBookDetail(book: Book) = intent {
        reduce {
            state.copy(
                bookDetail = book
            )
        }

    }

    fun initBookId(bookId: Long) = intent {
        reduce {
            state.copy(
                bookId = bookId
            )
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