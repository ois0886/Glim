package com.ssafy.glim.feature.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ssafy.glim.BuildConfig
import com.ssafy.glim.R
import com.ssafy.glim.core.ui.GlimErrorLoader
import com.ssafy.glim.core.ui.GlimSubcomposeAsyncImage
import com.ssafy.glim.feature.lock.component.SwipeButton
import com.ssafy.glim.ui.theme.GlimColor.LightRed
import com.ssafy.glim.ui.theme.GlimColor.MainColor
import java.time.format.DateTimeFormatter

@Composable
fun LockScreenContent(
    state: LockUiState,
    tick: () -> Unit,
    nextQuote: () -> Unit,
    prevQuote: () -> Unit,
    unlockMain: () -> Unit,
    saveGlim: () -> Unit,
    toggleLike: () -> Unit,
    viewBook: () -> Unit,
    viewQuote: () -> Unit,
) {
    LaunchedEffect(Unit) { tick() }

    val context = LocalContext.current
    val imageLoader = context.imageLoader
    LaunchedEffect(state.quotes) {
        state.quotes.forEach { quote ->
            val request = ImageRequest.Builder(context)
                .data(quote.quoteImageName)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            imageLoader.enqueue(request)
        }
    }
    val timeFmt = DateTimeFormatter.ofPattern("HH : mm")
    val dateFmt = DateTimeFormatter.ofPattern("M월 dd일")
    val dayFmt = DateTimeFormatter.ofPattern("EEE")

    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(Color.Black)
            .pointerInput(Unit) {
                var totalDragY = 0f
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        totalDragY += dragAmount
                    },
                    onDragEnd = {
                        when {
                            totalDragY < -100f -> nextQuote()
                            totalDragY > 100f -> prevQuote()
                        }
                        totalDragY = 0f
                    },
                )
            },
    ) {
        val currentQuote = state.quotes.getOrNull(state.currentIndex)
        if (currentQuote != null) {
            GlimSubcomposeAsyncImage(
                context = context,
                imageUrl = "${BuildConfig.BASE_URL}/images/${currentQuote.quoteImageName}",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            GlimErrorLoader(Modifier)
        }
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(top = 4.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = saveGlim) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = stringResource(R.string.save),
                    tint = MainColor.copy(alpha = 0.8f),
                )
            }
            IconButton(onClick = toggleLike) {
                Icon(
                    painter = if (currentQuote?.isLike == true) painterResource(R.drawable.ic_favorite_fill) else painterResource(R.drawable.ic_favorite),
                    contentDescription = stringResource(R.string.like),
                    tint = LightRed,
                )
            }
        }

        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = state.time.format(timeFmt),
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
            )
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = state.time.format(dateFmt),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                )
                Spacer(Modifier.width(4.dp))
                val fmtSec = state.time.format(dayFmt)
                Text(
                    text = stringResource(R.string.time_format_date, fmtSec),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                )
            }
        }
        /*
        Row(
            modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SwipeButton(
                isIcon = true,
                modifier =
                Modifier
                    .weight(1F)
                    .fillMaxWidth(),
                text = stringResource(R.string.read_book),
                isComplete = state.isComplete,
                onSwipe = viewBook,
                backgroundColor = Gray.copy(alpha = 0.4f),
                swipeDirection = SwipeDirection.RightToLeft,
                paintRes = R.drawable.ic_search,
            )
            Spacer(modifier = Modifier.weight(3F))
            SwipeButton(
                isIcon = true,
                modifier =
                Modifier
                    .weight(1F)
                    .fillMaxWidth(),
                text = stringResource(R.string.read_glim),
                isComplete = state.isComplete,
                onSwipe = viewQuote,
                backgroundColor = Gray.copy(alpha = 0.4f),
                paintRes = R.drawable.ic_shorts
            )
        }
*/
        SwipeButton(
            modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 36.dp, vertical = 110.dp),
            text = stringResource(R.string.lock_screen_slide_description),
            isComplete = state.isComplete,
            onSwipe = unlockMain,
        )
    }
}
