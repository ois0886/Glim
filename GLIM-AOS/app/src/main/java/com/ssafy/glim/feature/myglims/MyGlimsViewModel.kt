package com.ssafy.glim.feature.myglims

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class MyGlimsViewModel @Inject constructor(

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
