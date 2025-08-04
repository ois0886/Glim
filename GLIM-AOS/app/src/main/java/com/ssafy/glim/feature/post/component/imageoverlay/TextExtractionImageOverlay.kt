package com.ssafy.glim.feature.post.component.imageoverlay

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.glim.core.common.extensions.FILE_EXTENSION
import com.ssafy.glim.core.common.extensions.FILE_PREFIX
import com.ssafy.glim.core.common.extensions.toUri
import com.ssafy.glim.feature.shorts.rememberCaptureActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private object TextExtractionConstants {
    const val DEFAULT_BRUSH_RADIUS = 17.5f
    const val DEFAULT_BLACK_ALPHA = 0.4f
    const val PROCESSING_BLACK_ALPHA = 1f
    const val TRANSPARENT_ALPHA = 0f
    const val CAPTURE_DELAY_MS = 100L
    const val HEADER_PADDING = 4

    val BACKGROUND_COLOR = Color.Black
    val OVERLAY_COLOR = Color.Black
    val TRANSPARENT_COLOR = Color.Transparent
}

@Composable
fun TextExtractionImageOverlay(
    imageUri: Uri,
    onConfirm: (Uri) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onCancel() }

    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf(listOf<Offset>()) }
    var isProcessing by remember { mutableStateOf(false) }
    var blackAlpha by remember { mutableFloatStateOf(TextExtractionConstants.DEFAULT_BLACK_ALPHA) }
    var brushRadius by remember { mutableFloatStateOf(TextExtractionConstants.DEFAULT_BRUSH_RADIUS) }
    var captureFunction by remember { mutableStateOf<(suspend () -> Bitmap?)?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val allPoints = strokes.flatten() + currentStroke

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TextExtractionConstants.BACKGROUND_COLOR)
            .systemBarsPadding()
    ) {
        HeaderSection(
            onBackPress = onCancel,
            onConfirm = {
                handleConfirmClick(
                    isProcessing = isProcessing,
                    allPoints = allPoints,
                    captureFunction = captureFunction,
                    onProcessingChange = { isProcessing = it },
                    onAlphaChange = { blackAlpha = it },
                    context = context,
                    scope = scope,
                    onConfirm = onConfirm
                )
            }
        )

        ImageEditingArea(
            imageUri = imageUri,
            strokes = strokes,
            currentStroke = currentStroke,
            blackAlpha = blackAlpha,
            brushRadius = brushRadius,
            onStrokeStart = { offset -> currentStroke = listOf(offset) },
            onStrokeContinue = { change -> currentStroke = currentStroke + change.position },
            onStrokeEnd = {
                if (currentStroke.isNotEmpty()) {
                    strokes = strokes + listOf(currentStroke)
                    currentStroke = emptyList()
                }
            },
            onCaptureReady = { captureFunction = it },
            modifier = Modifier.weight(1f)
        )

        BottomControlsSection(
            brushRadius = brushRadius,
            onBrushRadiusChange = { brushRadius = it },
            onUndo = {
                handleUndo(strokes, currentStroke) { newStrokes, newCurrentStroke ->
                    strokes = newStrokes
                    currentStroke = newCurrentStroke
                }
            },
            onReset = { handleReset { strokes = emptyList(); currentStroke = emptyList() } }
        )
    }
}

@Composable
private fun HeaderSection(
    onBackPress: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    ImageOverlayHeader(
        modifier = modifier.padding(TextExtractionConstants.HEADER_PADDING.dp),
        onBackPress = onBackPress,
        onConfirm = onConfirm
    )
}

@Composable
private fun ImageEditingArea(
    imageUri: Uri,
    strokes: List<List<Offset>>,
    currentStroke: List<Offset>,
    blackAlpha: Float,
    brushRadius: Float,
    onStrokeStart: (Offset) -> Unit,
    onStrokeContinue: (androidx.compose.ui.input.pointer.PointerInputChange) -> Unit,
    onStrokeEnd: () -> Unit,
    onCaptureReady: (suspend () -> Bitmap?) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val brushRadiusPx = with(density) { brushRadius.dp.toPx() }
    val imageGraphicsLayer = rememberGraphicsLayer()
    val allPoints = strokes.flatten() + currentStroke

    val captureAction = rememberCaptureActions(
        graphicsLayer = imageGraphicsLayer,
        fileName = "${FILE_PREFIX}${System.currentTimeMillis()}${FILE_EXTENSION}",
    )

    LaunchedEffect(captureAction) {
        onCaptureReady { captureAction.getBitmap() }
    }

    Box(
        modifier = modifier.drawWithCache {
            onDrawWithContent {
                imageGraphicsLayer.record {
                    this@onDrawWithContent.drawContent()
                }
                drawLayer(imageGraphicsLayer)
            }
        }
    ) {
        OriginalImage(
            imageUri = imageUri,
            modifier = Modifier.fillMaxSize()
        )

        DrawingCanvas(
            allPoints = allPoints,
            blackAlpha = blackAlpha,
            brushRadiusPx = brushRadiusPx,
            onStrokeStart = onStrokeStart,
            onStrokeContinue = onStrokeContinue,
            onStrokeEnd = onStrokeEnd,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun OriginalImage(
    imageUri: Uri,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUri,
        contentDescription = "촬영된 이미지",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun DrawingCanvas(
    allPoints: List<Offset>,
    blackAlpha: Float,
    brushRadiusPx: Float,
    onStrokeStart: (Offset) -> Unit,
    onStrokeContinue: (androidx.compose.ui.input.pointer.PointerInputChange) -> Unit,
    onStrokeEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .pointerInput(brushRadiusPx) {
                detectDragGestures(
                    onDragStart = onStrokeStart,
                    onDrag = { change, _ -> onStrokeContinue(change) },
                    onDragEnd = onStrokeEnd
                )
            }
    ) {
        drawRect(
            color = TextExtractionConstants.OVERLAY_COLOR.copy(alpha = blackAlpha),
            size = size
        )

        allPoints.forEach { point ->
            drawCircle(
                color = TextExtractionConstants.TRANSPARENT_COLOR,
                radius = brushRadiusPx,
                center = point,
                blendMode = BlendMode.Clear
            )
        }
    }
}

@Composable
private fun BottomControlsSection(
    brushRadius: Float,
    onBrushRadiusChange: (Float) -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    ImageOverlayBottomControls(
        brushRadius = brushRadius,
        onBrushRadiusChange = onBrushRadiusChange,
        onUndo = onUndo,
        onReset = onReset,
        modifier = modifier
    )
}

private fun handleConfirmClick(
    isProcessing: Boolean,
    allPoints: List<Offset>,
    captureFunction: (suspend () -> Bitmap?)?,
    onProcessingChange: (Boolean) -> Unit,
    onAlphaChange: (Float) -> Unit,
    context: Context,
    scope: kotlinx.coroutines.CoroutineScope,
    onConfirm: (Uri) -> Unit
) {
    if (isProcessing || captureFunction == null) return

    onProcessingChange(true)
    onAlphaChange(
        if (allPoints.isNotEmpty())
            TextExtractionConstants.PROCESSING_BLACK_ALPHA
        else
            TextExtractionConstants.TRANSPARENT_ALPHA
    )

    scope.launch {
        try {
            delay(TextExtractionConstants.CAPTURE_DELAY_MS)
            val bitmap = captureFunction() ?: throw IllegalStateException("비트맵 생성 실패")
            val uri = bitmap.toUri(context) ?: throw IllegalStateException("uri 변환 실패")
            onConfirm(uri)
        } catch (e: Exception) {
            Log.d("ImageOverlay", e.message.toString())
        } finally {
            onProcessingChange(false)
        }
    }
}

private fun handleUndo(
    strokes: List<List<Offset>>,
    currentStroke: List<Offset>,
    onUpdate: (List<List<Offset>>, List<Offset>) -> Unit
) {
    when {
        strokes.isNotEmpty() -> onUpdate(strokes.dropLast(1), currentStroke)
        currentStroke.isNotEmpty() -> onUpdate(strokes, emptyList())
    }
}

private fun handleReset(onReset: () -> Unit) {
    onReset()
}

