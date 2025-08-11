package com.ssafy.glim.feature.post

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
import com.ssafy.glim.R
import com.ssafy.glim.core.common.utils.CameraType
import com.ssafy.glim.core.common.utils.rememberCameraWithPermission
import com.ssafy.glim.core.ui.DarkThemeScreen
import com.ssafy.glim.core.util.toCacheImageUri
import com.ssafy.glim.feature.post.component.PostContent
import com.ssafy.glim.feature.post.component.imageoverlay.TextExtractionImageOverlay
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

    val cameraState = rememberCameraWithPermission(
        onImageCaptured = { uri, type ->
            when (type) {
                CameraType.BACKGROUND_IMAGE -> viewModel.backgroundImageSelected(uri)
                CameraType.TEXT_RECOGNITION_IMAGE -> viewModel.textImageCaptured(uri)
            }
        },
        onPermissionDenied = {
            Toast.makeText(context, context.getString(R.string.camera_permission_deny_message), Toast.LENGTH_SHORT).show()
        },
        onCaptureFailed = {}
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            PostSideEffect.NavigateBack -> popBackStack()

            PostSideEffect.OpenTextImagePicker -> textImageLauncher.launch("image/*")

            PostSideEffect.OpenBackgroundImagePicker -> backgroundImageLauncher.launch("image/*")

            is PostSideEffect.OpenCamera -> {
                cameraState.launchCamera(sideEffect.type)
            }

            is PostSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is PostSideEffect.SaveGeneratedToCache -> {
                val uri = sideEffect.bitmap.toCacheImageUri(context)
                viewModel.backgroundImageSelected(uri)
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
                    updateTextFocusChanged = viewModel::updateTextFocusChanged,
                    onBackgroundClick = viewModel::onBackgroundClick,
                    onDragStart = viewModel::onDragStart,
                    onDragEnd = viewModel::onDragEnd,
                    onDrag = viewModel::updateTextPosition,
                    onIncreaseFontSize = viewModel::increaseFontSize,
                    onDecreaseFontSize = viewModel::decreaseFontSize,
                    onToggleBold = viewModel::toggleBold,
                    onToggleItalic = viewModel::toggleItalic,
                    startCameraAction = viewModel::startCameraAction,
                    onImageGenerateClick = viewModel::onImageGenerateClick,
                    onTextExtractionClick = viewModel::textExtractionClick,
                    onBackgroundImageClick = viewModel::backgroundImageClick,
                    onCompleteClick = viewModel::completeClick,
                    onConfirmExit = viewModel::confirmExit,
                    onCancelExit = viewModel::cancelExit,
                    onBackPress = viewModel::backPressed,
                    updateBottomSheetState = viewModel::updateBottomSheetState,
                    updateFontFamily = viewModel::updateFontFamily,
                    updateTextColor = viewModel::updateTextColor,
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
