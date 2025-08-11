package com.ssafy.glim.feature.post

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.common.utils.CameraType
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.usecase.image.ImageGenerateUseCase
import com.ssafy.glim.core.domain.usecase.book.GetCachedBookDetail
import com.ssafy.glim.core.domain.usecase.quote.CreateQuoteUseCase
import com.ssafy.glim.core.util.CaptureActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val createQuoteUseCase: CreateQuoteUseCase,
    private val getCachedBookDetail: GetCachedBookDetail,
    private val imageGenerateUseCase: ImageGenerateUseCase,
    private val imageProcessor: ImageProcessor,
) : ViewModel(), ContainerHost<PostState, PostSideEffect> {
    override val container: Container<PostState, PostSideEffect> = container(PostState())

    fun initialize(bookId: Long) = intent {
        runCatching { getCachedBookDetail(bookId) }
            .onSuccess {
                reduce { state.copy(book = it) }
            }
            .onFailure {
                Log.d("PostViewModel", "저장된 책 정보가 없거나 id가 일치하지 않습니다.")
            }
    }

    fun backPressed() =
        intent {
            if (state.showBottomSheet) {
                reduce { state.copy(showBottomSheet = false) }
            } else if (state.recognizedText.text.isNotEmpty() || state.backgroundImageUri != null || state.book != null) {
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

    fun textChanged(text: TextFieldValue) =
        intent {
            reduce { state.copy(recognizedText = text) }
        }

    // 텍스트 포커스 관리
    fun updateTextFocusChanged(focused: Boolean) = intent {
        if (focused) {
            if (state.isDragging) return@intent
            // 포커스될 때: 현재 위치를 원본으로 저장하고 상단으로 이동
            reduce {
                state.copy(
                    isFocused = focused,
                    originalTextPosition = state.textPosition,
                    textPosition = TextPosition(offsetY = -400f)
                )
            }
        } else {
            // 포커스 해제시: 원본 위치로 복원
            reduce {
                state.copy(
                    isFocused = focused,
                    textPosition = state.originalTextPosition
                )
            }
        }
    }

    fun onImageGenerateClick() = intent {
        if (state.recognizedText.text.isNotBlank()) {
            runCatching {
                reduce {
                    state.copy(
                        isLoading = true
                    )
                }
                imageGenerateUseCase.invoke(state.recognizedText.text)
            }
                .onSuccess {
                    reduce {
                        state.copy(
                            isLoading = false
                        )
                    }
                    postSideEffect(PostSideEffect.SaveGeneratedToCache(it.bitmap))
                }
                .onFailure {
                    reduce {
                        state.copy(
                            isLoading = false
                        )
                    }
                    postSideEffect(PostSideEffect.ShowToast(it.message ?: "에러가 발생했습니다."))
                }
        }
    }

    fun onBackgroundClick() =
        intent {
            reduce { state.copy(isFocused = false) }
        }

    fun updateBottomSheetState(isOpen: Boolean) = intent {
        reduce { state.copy(showBottomSheet = isOpen) }
    }

    // 드래그 시작
    fun onDragStart() = intent {
        if (state.recognizedText.text.isNotEmpty() && !state.isFocused) {
            reduce { state.copy(isDragging = true) }
        }
    }

    // 드래그 종료
    fun onDragEnd() = intent {
        reduce { state.copy(isDragging = false) }
    }

    // 텍스트 위치 업데이트
    fun updateTextPosition(
        deltaX: Float,
        deltaY: Float,
    ) = intent {
        if (state.recognizedText.text.isEmpty()) return@intent
        if (state.isFocused) return@intent

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

    fun textImageCaptured(uri: Uri?) = intent {
        reduce { state.copy(capturedTextExtractionImageUri = uri) }
    }

    fun textImageSelected(uri: Uri?) =
        intent {
            uri?.let {
                reduce {
                    state.copy(
                        capturedTextExtractionImageUri = null,
                        selectedImageUri = uri,
                        error = null,
                    )
                }

                try {
                    val result = imageProcessor.recognizeText(uri)
                    reduce {
                        state.copy(
                            recognizedText = TextFieldValue(result),
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

    fun backgroundImageSelected(uri: Uri) =
        intent {
            reduce { state.copy(backgroundImageUri = uri) }
        }

    fun startCameraAction(type: CameraType) = intent {
        postSideEffect(PostSideEffect.OpenCamera(type))
    }

    fun textExtractionClick() =
        intent {
            postSideEffect(PostSideEffect.OpenTextImagePicker)
        }

    fun backgroundImageClick() =
        intent {
            postSideEffect(PostSideEffect.OpenBackgroundImagePicker)
        }

    fun selectedBook(book: Book) =
        intent {
            reduce { state.copy(book = book, showBottomSheet = false) }
        }

    fun completeClick(captureActions: CaptureActions) =
        intent {
            if (state.isLoading) {
                postSideEffect(PostSideEffect.ShowToast("이미 업로드 중 입니다."))
                return@intent
            }
            reduce {
                state.copy(
                    isFocused = false,
                    isLoading = true
                )
            }
            val uri = state.backgroundImageUri
            val book = state.book

            if (uri == null) {
                postSideEffect(PostSideEffect.ShowToast("배경 이미지를 선택해주세요."))
                reduce { state.copy(isLoading = false) }
                return@intent
            }
            if (book == null) {
                postSideEffect(PostSideEffect.ShowToast("책 정보를 추가해 주세요."))
                reduce { state.copy(isLoading = false) }
                return@intent
            }
            try {
                delay(20) // 리컴포지션 대기 1프레임 16ms + 여유 시간
                Log.d("PostViewModel focus", "비트맵 생성")
                val bitmap = captureActions.getBitmap()
                runCatching {
                    createQuoteUseCase(
                        content = state.recognizedText.text,
                        isbn = book.isbn,
                        book = book,
                        bitmap = bitmap ?: throw IllegalArgumentException("Bitmap cannot be null"),
                    )
                }
                    .onSuccess {
                        postSideEffect(PostSideEffect.ShowToast("글림이 성공적으로 업로드되었습니다."))
                        postSideEffect(PostSideEffect.NavigateBack)
                    }
                    .onFailure {
                        Log.d("PostViewModel", "글림 업로드 실패: ${it.message}")
                        postSideEffect(PostSideEffect.ShowToast("글림 업로드에 실패했습니다."))
                    }
            } catch (e: Exception) {
                postSideEffect(PostSideEffect.ShowToast("업로드 중 오류가 발생했습니다: ${e.message}"))
            }

            reduce {
                state.copy(
                    isLoading = false
                )
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

    fun clearTextExtractionImage() = intent {
        reduce { state.copy(capturedTextExtractionImageUri = null) }
    }

    fun updateFontFamily(fontFamily: FontFamily) = intent {
        val currentStyle = state.textStyle
        reduce { state.copy(textStyle = currentStyle.copy(fontFamily = fontFamily)) }
    }

    fun updateTextColor(color: Color) = intent {
        val currentStyle = state.textStyle
        reduce { state.copy(textStyle = currentStyle.copy(textColor = color)) }
    }

    fun toggleVisibility() = intent {
        reduce { state.copy(visibility = !state.visibility) }
    }

    fun updateBackgroundImageAlpha(value: Float) = intent {
        reduce { state.copy(backgroundImageAlpha = value) }
    }
}
