package com.ssafy.glim.feature.post.component

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.feature.library.LibraryRoute
import com.ssafy.glim.feature.post.PostState
import com.ssafy.glim.feature.post.component.editabletext.EditableTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostContent(
    state: PostState,
    onTextChanged: (String) -> Unit,
    onTextFocusChanged: (Boolean) -> Unit,
    onBackgroundClick: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    onTextExtractionClick: () -> Unit,
    onBackgroundImageClick: () -> Unit,
    onCompleteClick: (Bitmap?) -> Unit,
    onConfirmExit: () -> Unit,
    onCancelExit: () -> Unit,
    updateBottomSheetState: (Boolean) -> Unit,
    selectedBook: (Book) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier =
        modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) {
                onBackgroundClick()
                focusManager.clearFocus()
            }
            .background(
                brush =
                Brush.linearGradient(
                    colors = listOf(Color(0x881C1B1F), Color(0xFF1C1B1F)),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                ),
            ),
    ) {
        if (state.showExitDialog) {
            ExitConfirmDialog(onCancelExit, onConfirmExit)
        }

        val imageGraphicsLayer = rememberGraphicsLayer()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawWithContent {
                        // AsyncImage만 GraphicsLayer에 기록
                        imageGraphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        drawLayer(imageGraphicsLayer)
                    }
                }
        ) {
            AsyncImage(
                model = state.backgroundImageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            EditableTextField(
                text = state.recognizedText,
                textStyle = state.textStyle,
                isFocused = state.isFocused,
                isDragging = state.isDragging,
                offsetX = state.textPosition.offsetX,
                offsetY = state.textPosition.offsetY,
                onTextChange = onTextChanged,
                onFocusChanged = onTextFocusChanged,
                onDragStart = onDragStart,
                onDragEnd = onDragEnd,
                onDrag = onDrag,
                onIncreaseFontSize = onIncreaseFontSize,
                onDecreaseFontSize = onDecreaseFontSize,
                onToggleBold = onToggleBold,
                onToggleItalic = onToggleItalic,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        ActionButtons(
            onTextExtractionClick = onTextExtractionClick,
            onBackgroundImageButtonClick = onBackgroundImageClick,
            onCreateTextClick = onTextFocusChanged,
            onCompleteClick = onCompleteClick,
            graphicsLayer = imageGraphicsLayer,
            modifier = Modifier.align(Alignment.BottomEnd),
        )

        BookInfoSection(
            modifier = Modifier.align(Alignment.BottomStart),
            book = state.book,
            onBookInfoClick = {
                updateBottomSheetState(true)
            },
        )

        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        )

        if (state.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { updateBottomSheetState(false) },
                containerColor = Color.White,
                contentColor = Color.Black,
                sheetState = bottomSheetState,
                tonalElevation = 8.dp,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.align(Alignment.BottomCenter),
                contentWindowInsets = { WindowInsets(0.dp, 0.dp, 0.dp, 0.dp) },
            ) {
                LibraryRoute(
                    padding = PaddingValues(0.dp),
                    popBackStack = {
                        updateBottomSheetState(false)
                    },
                    onBookSelected = { book ->
                        selectedBook(book)
                    },
                )
            }
        }
    }
}
