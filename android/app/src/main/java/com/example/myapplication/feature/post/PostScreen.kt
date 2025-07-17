package com.example.myapplication.feature.post

import PostIntent
import PostSideEffect
import PostState
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.feature.main.excludeSystemBars
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
    val focusManager = LocalFocusManager.current
    val imageProcessor = remember { ImageProcessor() }

    // System bars configuration
    SystemBarController.SetDarkSystemBars()

    // Image launchers
    val textImageLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri ->
            viewModel.handleIntent(PostIntent.OnTextImageSelected(uri, context))
        }

    val backgroundImageLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri ->
            viewModel.handleIntent(PostIntent.OnBackgroundImageSelected(uri))
        }

    // Background image loading
    val backgroundBitmap by produceState<android.graphics.Bitmap?>(null, state.backgroundImageUri) {
        value =
            state.backgroundImageUri?.let { uri ->
                imageProcessor.loadBitmap(context, uri)
            }
    }

    // Side effects handling
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            PostSideEffect.NavigateBack -> popBackStack()
            PostSideEffect.ClearFocus -> focusManager.clearFocus()
            PostSideEffect.OpenTextImagePicker -> textImageLauncher.launch("image/*")
            PostSideEffect.OpenBackgroundImagePicker -> backgroundImageLauncher.launch("image/*")
            is PostSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    // Back handler
    BackHandler {
        viewModel.handleIntent(PostIntent.OnBackPressed)
    }

    // Initialize
    LaunchedEffect(Unit) {
        viewModel.handleIntent(PostIntent.Initialize)
    }

    PostContent(
        state = state,
        backgroundBitmap = backgroundBitmap,
        onIntent = viewModel::handleIntent,
        modifier = Modifier.padding(padding.excludeSystemBars()),
    )
}

@Composable
private fun PostContent(
    state: PostState,
    backgroundBitmap: android.graphics.Bitmap?,
    onIntent: (PostIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalContentColor provides Color.White) {
        MaterialTheme(
            colorScheme = darkColorScheme(),
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
        ) {
            Box(
                modifier =
                    modifier
                        .fillMaxSize()
                        .background(
                            brush =
                                Brush.linearGradient(
                                    colors = listOf(Color(0x881C1B1F), Color(0xFF1C1B1F)),
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, Float.POSITIVE_INFINITY),
                                ),
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { onIntent(PostIntent.OnClearFocusClick) },
            ) {
                BackgroundImage(backgroundBitmap)

                EditableTextField(
                    text = state.recognizedText,
                    textStyle = state.textStyle,
                    isFocused = state.isFocused,
                    onIntent = onIntent,
                    modifier = Modifier.align(Alignment.Center),
                )

                ActionButtons(
                    isProcessing = state.isProcessing,
                    onIntent = onIntent,
                    modifier = Modifier.align(Alignment.BottomEnd),
                )

                BookInfoSection(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomEnd),
                )

                // Exit Dialog
                if (state.showExitDialog) {
                    AlertDialog(
                        onDismissRequest = { onIntent(PostIntent.CancelExit) },
                        title = { Text("나가시겠습니까?") },
                        text = { Text("작성 중인 내용이 사라집니다.") },
                        confirmButton = {
                            TextButton(
                                onClick = { onIntent(PostIntent.ConfirmExit) },
                            ) {
                                Text("나가기")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { onIntent(PostIntent.CancelExit) },
                            ) {
                                Text("취소")
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun BackgroundImage(bitmap: android.graphics.Bitmap?) {
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Background image",
            modifier =
                Modifier
                    .fillMaxSize()
                    .alpha(0.6f),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun BookInfoSection(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .padding(16.dp)
                .padding(end = 80.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.icon_post),
                contentDescription = null,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "책 정보 추가",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun createBackgroundBrush() =
    Brush.linearGradient(
        colors = listOf(Color(0x881C1B1F), Color(0xFF1C1B1F)),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY),
    )
