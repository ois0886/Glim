package com.ssafy.glim.feature.bookdetail

import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.domain.usecase.book.GetBookDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase

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
    }
}