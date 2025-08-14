package com.ssafy.glim.feature.post.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.unit.dp
import com.ssafy.glim.core.common.utils.CameraType
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.util.CaptureActions
import com.ssafy.glim.feature.post.PostState
import com.ssafy.glim.feature.search.SearchRoute

@Composable
fun PostUI(
    state: PostState,
    onImageGenerateClick: () -> Unit,
    startCameraAction: (CameraType) -> Unit,
    onTextExtractionClick: () -> Unit,
    onBackgroundImageClick: () -> Unit,
    updateTextFocusChanged: (Boolean) -> Unit,
    onCompleteClick: (CaptureActions) -> Unit,
    onBackPress: () -> Unit,
    onVisibilityClick: () -> Unit,
    onAlphaSlideValueChange: (Float) -> Unit,
    updateBottomSheetState: (Boolean) -> Unit,
    selectedBook: (Book) -> Unit,
    focusManager: FocusManager,
    imageGraphicsLayer: GraphicsLayer,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // ActionButtons - 오른쪽 하단
        ActionButtons(
            isTextNotEmpty = !state.recognizedText.text.isEmpty(),
            visibility = state.visibility,
            startCameraAction = startCameraAction,
            onImageGenerateClick = onImageGenerateClick,
            onTextExtractionClick = onTextExtractionClick,
            onBackgroundImageButtonClick = onBackgroundImageClick,
            onCreateTextClick = updateTextFocusChanged,
            onCompleteClick = onCompleteClick,
            onVisibilityClick = onVisibilityClick,
            clearFocus = { focusManager.clearFocus() },
            onBackPress = onBackPress,
            graphicsLayer = imageGraphicsLayer,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        if (state.visibility) {
            DarkGrayRoundedSurface(
                modifier = Modifier
                    .height(300.dp)
                    .padding(4.dp)
                    .align(Alignment.CenterStart)
            ) {
                VerticalSlider(
                    value = state.backgroundImageAlpha,
                    onValueChange = onAlphaSlideValueChange,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    valueRange = 0.1f..1f
                )
            }
        }

        // BookInfoSection - 왼쪽 하단
        BookInfoSection(
            modifier = Modifier.align(Alignment.BottomStart),
            book = state.book,
            onBookInfoClick = {
                updateBottomSheetState(true)
            }
        )

        if (state.showBottomSheet) {
            BookSearchBottomSheet(
                onDismiss = { updateBottomSheetState(false) },
                onBookSelected = selectedBook
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookSearchBottomSheet(
    onDismiss: () -> Unit,
    onBookSelected: (Book) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        contentColor = Color.Black,
        sheetState = bottomSheetState,
        tonalElevation = 8.dp,
        shape = MaterialTheme.shapes.large,
        contentWindowInsets = { WindowInsets(0.dp, 0.dp, 0.dp, 0.dp) }
    ) {
        SearchRoute(
            padding = PaddingValues(0.dp),
            popBackStack = onDismiss,
            onBookSelected = onBookSelected
        )
    }
}
