package com.example.myapplication.feature.reels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.domain.usecase.glim.GetGlimsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel
    @Inject
    constructor(
        private val getGlimsUseCase: GetGlimsUseCase,
//    private val toggleLikeUseCase: ToggleLikeUseCase
    ) : ViewModel(), ContainerHost<ReelsState, ReelsSideEffect> {
        override val container: Container<ReelsState, ReelsSideEffect> = container(ReelsState())

        fun toggleLike() =
            intent {
                val updatedGlims =
                    state.glims.map { glim ->
                        if (glim.id == state.currentGlimId) {
                            val newIsLike = !glim.isLike
                            glim.copy(
                                isLike = newIsLike,
                                likes = if (newIsLike) glim.likes + 1 else glim.likes - 1,
                            )
                        } else {
                            glim
                        }
                    }

                reduce { state.copy(glims = updatedGlims) }

                // 서버에 실제 업데이트
                viewModelScope.launch {
                    // TODO: API 호출
                    // toggleLikeUseCase(state.currentGlimId)
                }
            }

        fun onPageChanged(page: Int) =
            intent {
                // 페이지가 변경될 때 currentGlimId도 업데이트
                if (page >= 0 && page < state.glims.size) {
                    val currentGlim = state.glims[page]
                    reduce {
                        state.copy(
                            currentPage = page,
                            currentGlimId = currentGlim.id,
                        )
                    }
                }
            }

        fun onShareClick() =
            intent {
                state.currentGlim?.let {
                    postSideEffect(ReelsSideEffect.ShareGlim(it))
                }
            }

        fun onCaptureClick(fileName: String) =
            intent {
                try {
                    // 캡처 로직은 외부에서 처리하고 결과만 받음
                    postSideEffect(ReelsSideEffect.CaptureSuccess(fileName))
                } catch (e: Exception) {
                    postSideEffect(ReelsSideEffect.CaptureError("캡처에 실패했습니다: ${e.message}"))
                }
            }

        fun refresh() =
            intent {
                reduce {
                    state.copy(
                        glims = emptyList(),
                        currentPage = 0,
                        currentGlimId = -1,
                        hasMoreData = true,
                    )
                }
                loadInitialGlims()
            }

        private fun loadInitialGlims() =
            intent {
                reduce { state.copy(isLoading = true, error = null) }

                try {
                    getGlimsUseCase().collect { glims ->
                        reduce {
                            state.copy(
                                glims = glims,
                                currentGlimId = glims.firstOrNull()?.id ?: -1, // 첫 번째 글림의 ID 설정
                                isLoading = false,
                                hasMoreData = glims.size >= 10,
                            )
                        }
                    }
                } catch (e: Exception) {
                    reduce {
                        state.copy(
                            isLoading = false,
                            error = "글림을 불러오는데 실패했습니다.",
                        )
                    }
                    postSideEffect(ReelsSideEffect.ShowToast("글림을 불러오는데 실패했습니다."))
                }
            }
    }
