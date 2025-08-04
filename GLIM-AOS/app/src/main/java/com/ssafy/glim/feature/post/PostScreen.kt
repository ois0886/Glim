package com.ssafy.glim.feature.post

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.CircularProgressIndicator
import com.ssafy.glim.core.common.utils.rememberCameraWithPermission
import com.ssafy.glim.core.ui.DarkThemeScreen
import com.ssafy.glim.feature.post.component.imageoverlay.TextExtractionImageOverlay
import com.ssafy.glim.feature.post.component.PostContent
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
            viewModel.textImageCaptured(uri)
        }

    // 배경 이미지 선택
    val backgroundImageLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri ->
            if (uri != null) viewModel.backgroundImageSelected(uri)
        }

    // launch camera
    val cameraState = rememberCameraWithPermission(
        onImageCaptured = { uri ->
            Log.d("PostScreen", "텍스트 추출용 이미지 캡처됨")
            viewModel.textImageCaptured(uri)
        },
        onPermissionDenied = {
            Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        },
        onCaptureFailed = {
            Toast.makeText(context, "사진 촬영이 취소되었습니다.", Toast.LENGTH_SHORT).show()
        }
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            PostSideEffect.NavigateBack -> popBackStack()
            PostSideEffect.OpenTextImagePicker -> textImageLauncher.launch("image/*")
            PostSideEffect.OpenBackgroundImagePicker -> backgroundImageLauncher.launch("image/*")
            PostSideEffect.OpenTextExtractionCamera -> {
                // launch camera
                cameraState.launchCamera()
            }

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
        Box(modifier = Modifier.fillMaxSize()) {
            state.capturedTextExtractionImageUri?.let {
                TextExtractionImageOverlay(
                    imageUri = it,
                    onConfirm = viewModel::textImageSelected,
                    onCancel = viewModel::clearTextExtractionImage,
                )
            } ?: run {
                PostContent(
                    state = state,
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
                    onTextExtractionWithCameraClick = viewModel::textExtractionWithCameraClick,
                    onTextExtractionClick = viewModel::textExtractionClick,
                    onBackgroundImageClick = viewModel::backgroundImageClick,
                    onCompleteClick = viewModel::completeClick,
                    onConfirmExit = viewModel::confirmExit,
                    onCancelExit = viewModel::cancelExit,
                    onBackPress = viewModel::backPressed,
                    updateBottomSheetState = viewModel::updateBottomSheetState,
                    selectedBook = viewModel::selectedBook
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(52.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}
