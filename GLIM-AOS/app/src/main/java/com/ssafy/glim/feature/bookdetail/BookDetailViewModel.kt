package com.ssafy.glim.feature.bookdetail

import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.domain.usecase.book.GetBookDetailUseCase
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
    private val navigator: Navigator
) : ViewModel(), ContainerHost<BookDetailState, BookDetailSideEffect> {

    override val container: Container<BookDetailState, BookDetailSideEffect> = container(BookDetailState())

    init {
        loadBookDetail("1")
    }

    private fun loadBookDetail(bookId: String) = intent {
        getBookDetailUseCase(bookId).collect {
            reduce {
                state.copy(
                    bookDetail = it
                )
            }
        }
    }

    fun onClickQuote(quoteId: Long) = intent {
        navigator.navigate(MainTab.REELS.route)
    }

    fun openUrl() = intent {
        postSideEffect(BookDetailSideEffect.OpenUrl(state.bookDetail.marketUrl))
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