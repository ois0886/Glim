package com.ssafy.glim.feature.lock

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ssafy.glim.R
import com.ssafy.glim.core.ui.ImageCustomLoader
import com.ssafy.glim.feature.lock.component.SwipeButton
import com.ssafy.glim.feature.lock.component.SwipeDirection
import com.ssafy.glim.feature.main.MainActivity
import com.ssafy.glim.ui.theme.GlimColor.LightRed
import com.ssafy.glim.ui.theme.GlimColor.MainColor
import com.ssafy.glim.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
@SuppressLint("SourceLockedOrientationActivity")
class LockScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            (getSystemService(KeyguardManager::class.java))
                ?.requestDismissKeyguard(this, null)
        }
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            // You can hide the caption bar even when the other system bars are visible.
            // To account for this, explicitly check the visibility of navigationBars()
            // and statusBars() rather than checking the visibility of systemBars().
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: LockViewModel = hiltViewModel()
                val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

                viewModel.collectSideEffect { effect ->
                    when (effect) {
                        is LockSideEffect.Unlock -> finish()
                        is LockSideEffect.ShowToast -> Toast.makeText(
                            this,
                            this.getString(effect.messageRes),
                            Toast.LENGTH_SHORT
                        ).show()

                        is LockSideEffect.NavigateBook -> {
                            this.startActivity(
                                Intent(this, MainActivity::class.java).apply {
                                    putExtra("nav_route", "book")
                                }
                            )
                            (this as? Activity)?.finish()
                        }
                        is LockSideEffect.NavigateQuotes -> {
                            this.startActivity(
                                Intent(this, MainActivity::class.java).apply {
                                    putExtra("nav_route", "glim")
                                }
                            )
                            (this as? Activity)?.finish()
                        }
                    }
                }

                LockScreenContent(
                    state = state,
                    tick = viewModel::tick,
                    nextQuote = viewModel::nextQuote,
                    prevQuote = viewModel::prevQuote,
                    unlockMain = viewModel::unlockMain,
                    saveGlim = viewModel::saveGlim,
                    favoriteGlim = viewModel::favoriteGlim,
                    viewBook = viewModel::viewBook,
                    viewQuote = viewModel::viewQuote,
                )
            }
        }
    }
}

@Composable
fun LockScreenContent(
    state: LockUiState,
    tick: () -> Unit,
    nextQuote: () -> Unit,
    prevQuote: () -> Unit,
    unlockMain: () -> Unit,
    saveGlim: () -> Unit,
    favoriteGlim: () -> Unit,
    viewBook: () -> Unit,
    viewQuote: () -> Unit,
) {
    LaunchedEffect(Unit) { tick() }

    val context = LocalContext.current
    val imageLoader = context.imageLoader
    LaunchedEffect(state.quotes) {
        state.quotes.forEach { quote ->
            val request = ImageRequest.Builder(context)
                .data(quote.imgUrl)
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
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentQuote.imgUrl)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                imageLoader = imageLoader,
                loading = {
                    ImageCustomLoader(Modifier)
                },
                error = {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Gray.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.example_glim_4),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            )
        } else {
            Image(
                painter = painterResource(R.drawable.example_glim_2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
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
            IconButton(onClick = favoriteGlim) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorite),
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
                paintRes = R.drawable.ic_library,
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
                paintRes = R.drawable.ic_reels
            )
        }

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
