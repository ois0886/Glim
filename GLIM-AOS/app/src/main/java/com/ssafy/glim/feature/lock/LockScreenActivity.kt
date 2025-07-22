package com.ssafy.glim.feature.lock

import android.annotation.SuppressLint
import android.app.KeyguardManager
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.glim.R
import com.ssafy.glim.feature.lock.component.SwipeButton
import com.ssafy.glim.feature.lock.component.SwipeDirection
import com.ssafy.glim.ui.theme.GlimColor.LightBlue
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
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: LockViewModel = hiltViewModel()
                val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()


                viewModel.collectSideEffect { effect ->
                    when (effect) {
                        is LockSideEffect.Unlock         -> finish()
                        is LockSideEffect.ShowSaveToast  -> showToast("저장되었습니다")
                        is LockSideEffect.ShowFavoriteToast -> showToast("좋아요!")
                        is LockSideEffect.NavigateBook   -> Unit/* 북 디테일로 이동 */
                        is LockSideEffect.NavigateQuotes -> Unit/* 글귀 화면으로 이동 */
                    }
                }

                LockScreenContent(
                    state        = state,
                    tick         = viewModel::tick,
                    nextQuote    = viewModel::nextQuote,
                    unlockMain   = viewModel::unlockMain,
                    saveGlim     = viewModel::saveGlim,
                    favoriteGlim = viewModel::favoriteGlim,
                    viewBook     = viewModel::viewBook,
                    viewQuote    = viewModel::viewQuote,
                )
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun LockScreenContent(
    state: LockUiState,
    tick:        () -> Unit,
    nextQuote:   () -> Unit,
    unlockMain:  () -> Unit,
    saveGlim:    () -> Unit,
    favoriteGlim:() -> Unit,
    viewBook:    () -> Unit,
    viewQuote:   () -> Unit,
) {
    LaunchedEffect(Unit) { tick() }

    val timeFmt = DateTimeFormatter.ofPattern("HH : mm")
    val dateFmt = DateTimeFormatter.ofPattern("M월 dd일")
    val dayFmt = DateTimeFormatter.ofPattern("EEE")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .navigationBarsPadding()
            .pointerInput(Unit) {
                var totalDragY = 0f
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount -> totalDragY += dragAmount },
                    onDragEnd = {
                        if (totalDragY < -100f) nextQuote()
                        totalDragY = 0f
                    }
                )
            }
    ) {
        val currentQuote = state.quotes.getOrNull(state.currentIndex)
        if (currentQuote != null) {
            AsyncImage(
                model = currentQuote.imgUrl,              // domain 모델 필드명으로 변경
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.example_glim_2),
                placeholder = painterResource(R.drawable.example_glim_2)
            )
        } else {
            // 아직 로드되지 않았거나 인덱스 초과 시 기본 배경
            Image(
                painter = painterResource(R.drawable.example_glim_2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(top = 4.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = saveGlim) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = "저장",
                    tint = MainColor.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = favoriteGlim) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorite),
                    contentDescription = "좋아요",
                    tint = LightRed
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.time.format(timeFmt),
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Text(
                    text = state.time.format(dateFmt),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Spacer(Modifier.width(4.dp))
                val fmtSec = state.time.format(dayFmt)
                Text(
                    text = "($fmtSec)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SwipeButton(
                isIcon = true,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth(),
                text = "도서 보기",
                isComplete = state.isComplete,
                onSwipe = viewBook,
                backgroundColor = LightBlue,
                swipeDirection = SwipeDirection.RightToLeft,
                paintRes = R.drawable.ic_library
            )
            Spacer(modifier =Modifier.weight(3F))
            SwipeButton(
                isIcon = true,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth(),
                text = "글귀 보기",
                isComplete = state.isComplete,
                onSwipe = viewQuote,
                backgroundColor = LightBlue,
                paintRes = R.drawable.ic_reels
            )
        }

        SwipeButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 48.dp),
            text = "밀어서 잠금 해제 >>",
            isComplete = state.isComplete,
            onSwipe = unlockMain,
        )
    }
}