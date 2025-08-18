package com.example.myapplication.feature.post
import PostIntent
import PostSideEffect
import PostState
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PostViewModel
    @Inject
    constructor(
        private val imageProcessor: ImageProcessor,
    ) : ViewModel(), ContainerHost<PostState, PostSideEffect> {
        override val container: Container<PostState, PostSideEffect> = container(PostState())

        fun handleIntent(intent: PostIntent) =
            intent {
                when (intent) {
                    PostIntent.Initialize -> handleInitialize()
                    PostIntent.OnBackPressed -> handleBackPressed()
                    PostIntent.ConfirmExit -> handleConfirmExit()
                    PostIntent.CancelExit -> handleCancelExit()
                    is PostIntent.OnTextChanged -> handleTextChanged(intent.text)
                    is PostIntent.OnFocusChanged -> handleFocusChanged(intent.focused)
                    is PostIntent.OnTextImageSelected -> handleTextImageSelected(intent.uri, intent.context)
                    is PostIntent.OnBackgroundImageSelected -> handleBackgroundImageSelected(intent.uri)
                    PostIntent.OnTextExtractionClick -> handleTextExtractionClick()
                    PostIntent.OnBackgroundImageClick -> handleBackgroundImageClick()
                    PostIntent.OnCompleteClick -> handleCompleteClick()
                    PostIntent.OnClearFocusClick -> handleClearFocusClick()
                    PostIntent.IncreaseFontSize -> handleIncreaseFontSize()
                    PostIntent.DecreaseFontSize -> handleDecreaseFontSize()
                    PostIntent.ToggleBold -> handleToggleBold()
                    PostIntent.ToggleItalic -> handleToggleItalic()
                    else -> {}
                }
            }

        private fun handleInitialize() =
            intent {
                // 초기화 로직
            }

        private fun handleBackPressed() =
            intent {
                if (state.recognizedText.isNotEmpty()) {
                    reduce { state.copy(showExitDialog = true) }
                } else {
                    postSideEffect(PostSideEffect.NavigateBack)
                }
            }

        private fun handleConfirmExit() =
            intent {
                reduce { state.copy(showExitDialog = false) }
                postSideEffect(PostSideEffect.NavigateBack)
            }

        private fun handleCancelExit() =
            intent {
                reduce { state.copy(showExitDialog = false) }
            }

        private fun handleTextChanged(text: String) =
            intent {
                reduce { state.copy(recognizedText = text) }
            }

        private fun handleFocusChanged(focused: Boolean) =
            intent {
                reduce { state.copy(isFocused = focused) }
                if (!focused) {
                    postSideEffect(PostSideEffect.ClearFocus)
                }
            }

        private fun handleTextImageSelected(
            uri: Uri?,
            context: Context,
        ) = intent {
            uri?.let {
                reduce {
                    state.copy(
                        selectedImageUri = uri,
                        isProcessing = true,
                        error = null,
                    )
                }

                try {
                    val result = imageProcessor.recognizeText(context, uri)
                    reduce {
                        state.copy(
                            recognizedText = result,
                            isProcessing = false,
                        )
                    }
                } catch (e: Exception) {
                    reduce {
                        state.copy(
                            isProcessing = false,
                            error = "텍스트 인식 실패: ${e.message}",
                        )
                    }
                    postSideEffect(PostSideEffect.ShowToast("텍스트 인식에 실패했습니다"))
                }
            }
        }

        private fun handleBackgroundImageSelected(uri: Uri?) =
            intent {
                reduce { state.copy(backgroundImageUri = uri) }
            }

        private fun handleTextExtractionClick() =
            intent {
                postSideEffect(PostSideEffect.OpenTextImagePicker)
            }

        private fun handleBackgroundImageClick() =
            intent {
                postSideEffect(PostSideEffect.OpenBackgroundImagePicker)
            }

        private fun handleCompleteClick() =
            intent {
                // 완료 로직 구현
                postSideEffect(PostSideEffect.NavigateBack)
            }

        private fun handleClearFocusClick() =
            intent {
                postSideEffect(PostSideEffect.ClearFocus)
            }

        private fun handleIncreaseFontSize() =
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

        private fun handleDecreaseFontSize() =
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

        private fun handleToggleBold() =
            intent {
                val currentStyle = state.textStyle
                reduce {
                    state.copy(
                        textStyle = currentStyle.copy(isBold = !currentStyle.isBold),
                    )
                }
            }

        private fun handleToggleItalic() =
            intent {
                val currentStyle = state.textStyle
                reduce {
                    state.copy(
                        textStyle = currentStyle.copy(isItalic = !currentStyle.isItalic),
                    )
                }
            }
    }
