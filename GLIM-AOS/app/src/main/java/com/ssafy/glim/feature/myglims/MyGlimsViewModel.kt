package com.ssafy.glim.feature.myglims

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.glim.core.domain.usecase.quote.GetMyLikedQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.GetMyUploadQuoteUseCase
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class MyGlimsViewModel @Inject constructor(
    private val getMyUploadQuoteUseCase: GetMyUploadQuoteUseCase,
    private val getMyLikedQuoteUseCase: GetMyLikedQuoteUseCase,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<MyGlimsUiState, MyGlimsSideEffect> {
    override val container =
        container<MyGlimsUiState, MyGlimsSideEffect>(initialState = MyGlimsUiState())

    fun loadMyGlims(listType: MyGlimsType) = intent {
        reduce { state.copy(isLoading = true, currentListType = listType, errorMessage = null) }

        runCatching {
            when (listType) {
                MyGlimsType.LIKED -> getMyLikedQuoteUseCase()
                MyGlimsType.UPLOADED -> getMyUploadQuoteUseCase()
            }
        }.onSuccess { quotes ->
            reduce {
                state.copy(
                    myGlims = quotes,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }.onFailure { exception ->
            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = exception.message
                )
            }
            postSideEffect(MyGlimsSideEffect.ShowToast("데이터를 불러오는데 실패했습니다."))
        }
    }

    fun navigateToQuote(quoteId: Long) =
        viewModelScope.launch {
            navigator.navigate(BottomTabRoute.Shorts(quoteId))
        }

    fun navigateToBookDetail(bookId: Long) =
        viewModelScope.launch {
            Log.d("HomeViewModel", "Navigating to BookDetail with bookId: $bookId")
            navigator.navigate(Route.BookDetail(bookId = bookId))
        }
}
