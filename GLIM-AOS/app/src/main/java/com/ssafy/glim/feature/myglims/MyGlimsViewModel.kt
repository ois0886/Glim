package com.ssafy.glim.feature.myglims

import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.domain.usecase.quote.GetMyLikedQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.GetMyUploadQuoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class MyGlimsViewModel @Inject constructor(
    private val getMyUploadQuoteUseCase: GetMyUploadQuoteUseCase,
    private val getMyLikedQuoteUseCase: GetMyLikedQuoteUseCase
) : ViewModel(), ContainerHost<MyGlimsUiState, MyGlimsSideEffect> {
    override val container =
        container<MyGlimsUiState, MyGlimsSideEffect>(initialState = MyGlimsUiState())

    fun loadMyGlims(listType: MyGlimsType) =
        intent {
            reduce { state.copy(isLoading = true, currentListType = listType) }
            postSideEffect(MyGlimsSideEffect.ShowToast("데이터를 불러오는데 실패했습니다."))
        }

    fun toggleLike(glimId: Long) =
        intent {
            postSideEffect(MyGlimsSideEffect.ShowToast("좋아요 처리에 실패했습니다."))
        }
}
