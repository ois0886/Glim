package com.ssafy.glim.feature.glimlist

import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class GlimListViewModel
    @Inject
    constructor(
        private val navigator: Navigator,
    ) : ViewModel(), ContainerHost<GlimListUiState, GlimListSideEffect> {
        override val container =
            container<GlimListUiState, GlimListSideEffect>(initialState = GlimListUiState())

        private val mockLikedGlims =
            listOf(
                GlimItem(
                    id = 1,
                    content = "어쩐 더이상 중공지도 않아. 왜지. 왜 나는 이 현재 말라가는 거지.",
                    author = "허닝썩 시간",
                    likeCount = 1247,
                    isLiked = true,
                ),
                GlimItem(
                    id = 2,
                    content = "인생은 짧고 예술은 길다. 하지만 우리가 해야 할 일은 지금 이 순간을 소중히 여기는 것이다.",
                    author = "히포크라테스",
                    likeCount = 892,
                    isLiked = false,
                ),
                GlimItem(
                    id = 3,
                    content = "성공은 1%의 영감과 99%의 노력으로 이루어진다.",
                    author = "토마스 에디슨",
                    likeCount = 1534,
                    isLiked = true,
                ),
            )

        private val mockUploadedGlims =
            listOf(
                GlimItem(
                    id = 4,
                    content = "오늘 하루도 최선을 다했다. 내일은 더 나은 하루가 될 것이다.",
                    author = "나",
                    likeCount = 45,
                    isLiked = false,
                ),
                GlimItem(
                    id = 5,
                    content = "작은 변화가 큰 결과를 만든다. 포기하지 말고 계속 도전하자.",
                    author = "나",
                    likeCount = 78,
                    isLiked = true,
                ),
            )

        fun loadGlimList(listType: GlimListType) =
            intent {
                reduce { state.copy(isLoading = true, currentListType = listType) }

                try {
                    // 실제로는 리포지토리에서 데이터를 가져와야 함
                    delay(1000) // 로딩 시뮬레이션

                    val glimList =
                        when (listType) {
                            GlimListType.LIKED -> mockLikedGlims
                            GlimListType.UPLOADED -> mockUploadedGlims
                        }

                    reduce {
                        state.copy(
                            glimList = glimList,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                } catch (e: Exception) {
                    reduce {
                        state.copy(
                            isLoading = false,
                            errorMessage = e.message,
                        )
                    }
                    postSideEffect(GlimListSideEffect.ShowToast("데이터를 불러오는데 실패했습니다."))
                }
            }

        fun toggleLike(glimId: Long) =
            intent {
                val currentList = state.glimList
                val updatedList =
                    currentList.map { glim ->
                        if (glim.id == glimId) {
                            glim.copy(
                                isLiked = !glim.isLiked,
                                likeCount = if (glim.isLiked) glim.likeCount - 1 else glim.likeCount + 1,
                            )
                        } else {
                            glim
                        }
                    }

                reduce { state.copy(glimList = updatedList) }

                // 실제로는 서버에 좋아요 상태 업데이트 API 호출
                try {
                    // repository.toggleLike(glimId)
                    val message =
                        updatedList.find { it.id == glimId }?.let { glim ->
                            if (glim.isLiked) "좋아요를 눌렀습니다." else "좋아요를 취소했습니다."
                        } ?: ""
                    postSideEffect(GlimListSideEffect.ShowToast(message))
                } catch (e: Exception) {
                    postSideEffect(GlimListSideEffect.ShowToast("좋아요 처리에 실패했습니다."))
                }
            }
    }
