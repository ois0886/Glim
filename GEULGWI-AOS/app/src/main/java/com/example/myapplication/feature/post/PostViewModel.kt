package com.example.myapplication.feature.post

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.myapplication.core.domain.model.GlimInput
import com.example.myapplication.core.domain.usecase.glim.UploadGlimUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PostViewModel
    @Inject
    constructor(
        private val uploadGlimUseCase: UploadGlimUseCase,
        private val imageProcessor: ImageProcessor,
    ) : ViewModel(), ContainerHost<PostState, PostSideEffect> {
        override val container: Container<PostState, PostSideEffect> = container(PostState())

        fun initialize() =
            intent {
                // 초기화 로직
            }

        fun backPressed() =
            intent {
                if (state.recognizedText.isNotEmpty()) {
                    reduce { state.copy(showExitDialog = true) }
                } else {
                    postSideEffect(PostSideEffect.NavigateBack)
                }
            }

        fun confirmExit() =
            intent {
                reduce { state.copy(showExitDialog = false) }
                postSideEffect(PostSideEffect.NavigateBack)
            }

        fun cancelExit() =
            intent {
                reduce { state.copy(showExitDialog = false) }
            }

        fun textChanged(text: String) =
            intent {
                reduce { state.copy(recognizedText = text) }
            }

        // 텍스트 포커스 관리
        fun onTextFocusChanged(focused: Boolean) =
            intent {
                reduce { state.copy(isFocused = focused) }
            }

        // 배경 클릭 시 포커스 해제
        fun onBackgroundClick() =
            intent {
                reduce { state.copy(isFocused = false) }
            }

        // 드래그 시작
        fun onDragStart() =
            intent {
                reduce { state.copy(isDragging = true) }
            }

        // 드래그 종료
        fun onDragEnd() =
            intent {
                reduce { state.copy(isDragging = false) }
            }

        // 텍스트 위치 업데이트
        fun updateTextPosition(
            deltaX: Float,
            deltaY: Float,
        ) = intent {
            val currentPosition = state.textPosition
            reduce {
                state.copy(
                    textPosition =
                        currentPosition.copy(
                            offsetX = currentPosition.offsetX + deltaX,
                            offsetY = currentPosition.offsetY + deltaY,
                        ),
                )
            }
        }

        fun textImageSelected(uri: Uri?) =
            intent {
                uri?.let {
                    reduce {
                        state.copy(
                            selectedImageUri = uri,
                            error = null,
                        )
                    }

                    try {
                        val result = imageProcessor.recognizeText(uri)
                        reduce {
                            state.copy(
                                recognizedText = result,
                            )
                        }
                    } catch (e: Exception) {
                        reduce {
                            state.copy(
                                error = "텍스트 인식 실패: ${e.message}",
                            )
                        }
                        postSideEffect(PostSideEffect.ShowToast("텍스트 인식에 실패했습니다"))
                    }
                }
            }

        fun backgroundImageSelected(uri: Uri?) =
            intent {
                reduce { state.copy(backgroundImageUri = uri) }
            }

        fun textExtractionClick() =
            intent {
                postSideEffect(PostSideEffect.OpenTextImagePicker)
            }

        fun backgroundImageClick() =
            intent {
                postSideEffect(PostSideEffect.OpenBackgroundImagePicker)
            }

        fun completeClick() =
            intent {
                state.backgroundImageUri?.let { uri ->
                    try {
                        val bitmap =
                            imageProcessor.uriToBitmap(uri)
                                ?: throw IllegalStateException("이미지를 불러올 수 없습니다.")

                        uploadGlimUseCase(
                            GlimInput(bitmap, state.bookId),
                        ).collect { isSuccess ->
                            if (isSuccess) {
                                postSideEffect(PostSideEffect.ShowToast("글림이 성공적으로 업로드되었습니다."))
                                postSideEffect(PostSideEffect.NavigateBack)
                            } else {
                                postSideEffect(PostSideEffect.ShowToast("글림 업로드에 실패했습니다."))
                            }
                        }
                    } catch (e: Exception) {
                        postSideEffect(PostSideEffect.ShowToast("업로드 중 오류가 발생했습니다: ${e.message}"))
                    }
                } ?: run {
                    postSideEffect(PostSideEffect.ShowToast("배경 이미지를 선택해주세요."))
                }
            }

        fun increaseFontSize() =
            intent {
                val currentStyle = state.textStyle
                if (currentStyle.fontSize < 32f) {
                    reduce {
                        state.copy(
                            textStyle = currentStyle.copy(fontSize = currentStyle.fontSize + 2f),
                        )
                    }
                }
            }

        fun decreaseFontSize() =
            intent {
                val currentStyle = state.textStyle
                if (currentStyle.fontSize > 12f) {
                    reduce {
                        state.copy(
                            textStyle = currentStyle.copy(fontSize = currentStyle.fontSize - 2f),
                        )
                    }
                }
            }

        fun toggleBold() =
            intent {
                val currentStyle = state.textStyle
                reduce {
                    state.copy(
                        textStyle = currentStyle.copy(isBold = !currentStyle.isBold),
                    )
                }
            }

        fun toggleItalic() =
            intent {
                val currentStyle = state.textStyle
                reduce {
                    state.copy(
                        textStyle = currentStyle.copy(isItalic = !currentStyle.isItalic),
                    )
                }
            }
    }
