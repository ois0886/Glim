package com.ssafy.glim.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.glim.core.domain.model.CurationType
import com.ssafy.glim.core.domain.usecase.curation.GetMainCurationsUseCase
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.home.model.HomeSectionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMainCurationsUseCase: GetMainCurationsUseCase,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<HomeUiState, HomeSideEffect> {
    override val container = container<HomeUiState, HomeSideEffect>(initialState = HomeUiState())

    fun navigateToQuote(quoteId: Long) =
        viewModelScope.launch {
            navigator.navigate(BottomTabRoute.Shorts(quoteId))
        }

    fun navigateToBookDetail(bookId: Long) =
        viewModelScope.launch {
            Log.d("HomeViewModel", "Navigating to BookDetail with bookId: $bookId")
            navigator.navigate(Route.BookDetail(bookId = bookId))
        }

    fun refreshHome() = intent {
        reduce { state.copy(isRefreshing = true) }

        runCatching { getMainCurationsUseCase() }
            .onSuccess { curations ->
                val sections = curations.map { curation ->
                    when (curation.type) {
                        CurationType.QUOTE -> HomeSectionUiModel.QuoteSection(
                            id = curation.id?.toString().orEmpty(),
                            title = curation.title,
                            quotes = curation.contents.quote
                        )

                        CurationType.BOOK -> HomeSectionUiModel.BookSection(
                            id = curation.id?.toString().orEmpty(),
                            title = curation.title,
                            books = curation.contents.book
                        )
                    }
                }

                reduce {
                    state.copy(
                        isRefreshing = false,
                        sections = sections
                    )
                }
            }
            .onFailure { throwable ->
                reduce { state.copy(isRefreshing = false) }
                postSideEffect(HomeSideEffect.ShowError("새로고침에 실패했습니다: ${throwable.message}"))
            }
    }

    init {
        loadCurationData()
    }

    private fun loadCurationData() = intent {
        reduce { state.copy(isLoading = true) }
        runCatching { getMainCurationsUseCase() }
            .onSuccess { curations ->
                val sections = curations.map { curation ->
                    when (curation.type) {
                        CurationType.QUOTE -> HomeSectionUiModel.QuoteSection(
                            id = curation.id?.toString().orEmpty(),
                            title = curation.title,
                            quotes = curation.contents.quote
                        )

                        CurationType.BOOK -> HomeSectionUiModel.BookSection(
                            id = curation.id?.toString().orEmpty(),
                            title = curation.title,
                            books = curation.contents.book
                        )
                    }
                }

                reduce {
                    state.copy(
                        isLoading = false,
                        sections = sections
                    )
                }
            }
            .onFailure { throwable ->
                reduce { state.copy(isLoading = false) }
                postSideEffect(HomeSideEffect.ShowError(throwable.message ?: "알 수 없는 에러"))
            }
    }
}
