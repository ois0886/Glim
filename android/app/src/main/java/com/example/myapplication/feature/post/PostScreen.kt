package com.example.myapplication.feature.post

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.core.ui.DarkThemeScreen
import com.example.myapplication.feature.main.excludeSystemBars
import com.example.myapplication.feature.post.component.PostContent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun PostRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    viewModel: PostViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    // 이미지 텍스트 추출
    val textImageLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri ->
            viewModel.textImageSelected(uri)
        }

    // 배경 이미지 선택
    val backgroundImageLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri ->
            viewModel.backgroundImageSelected(uri)
        }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            PostSideEffect.NavigateBack -> popBackStack()
            PostSideEffect.OpenTextImagePicker -> textImageLauncher.launch("image/*")
            PostSideEffect.OpenBackgroundImagePicker -> backgroundImageLauncher.launch("image/*")
            is PostSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    BackHandler {
        viewModel.backPressed()
    }

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    DarkThemeScreen {
        PostContent(
            recognizedText = state.recognizedText,
            textStyle = state.textStyle,
            backgroundImageUri = state.backgroundImageUri,
            showExitDialog = state.showExitDialog,
            isFocused = state.isFocused,
            isDragging = state.isDragging,
            offsetX = state.textPosition.offsetX,
            offsetY = state.textPosition.offsetY,
            onTextChanged = viewModel::textChanged,
            onTextFocusChanged = viewModel::onTextFocusChanged,
            onBackgroundClick = viewModel::onBackgroundClick,
            onDragStart = viewModel::onDragStart,
            onDragEnd = viewModel::onDragEnd,
            onDrag = viewModel::updateTextPosition,
            onIncreaseFontSize = viewModel::increaseFontSize,
            onDecreaseFontSize = viewModel::decreaseFontSize,
            onToggleBold = viewModel::toggleBold,
            onToggleItalic = viewModel::toggleItalic,
            onTextExtractionClick = viewModel::textExtractionClick,
            onBackgroundImageClick = viewModel::backgroundImageClick,
            onCompleteClick = viewModel::completeClick,
            onConfirmExit = viewModel::confirmExit,
            onCancelExit = viewModel::cancelExit,
            modifier = Modifier.padding(padding.excludeSystemBars()),
        )
    }
}
