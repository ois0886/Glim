package com.ssafy.glim.feature.lock

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.ssafy.glim.R
import com.ssafy.glim.feature.lock.component.SwipeButton
import com.ssafy.glim.feature.lock.component.SwipeDirection
import com.ssafy.glim.ui.theme.GlimColor.LightBlue
import com.ssafy.glim.ui.theme.GlimColor.LightRed
import com.ssafy.glim.ui.theme.GlimColor.MainColor
import com.ssafy.glim.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
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
                LockScreenContent()
            }
        }
    }

    @Composable
    fun LockScreenContent() {
        // 언락 플래그
        var isComplete by remember { mutableStateOf(false) }
        // 배경 이미지 리소스
        var currentRes by remember { mutableIntStateOf(nextDrawableRes()) }
        // 실시간 시간·날짜 상태
        var now by remember { mutableStateOf(LocalDateTime.now()) }

        // 1초마다 now 갱신
        LaunchedEffect(Unit) {
            while (true) {
                now = LocalDateTime.now()
                delay(1000L)
            }
        }

        // 포맷터
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
                            if (totalDragY < -100f) currentRes = nextDrawableRes()
                            totalDragY = 0f
                        }
                    )
                }
        ) {
            // 배경 이미지
            Image(
                painter = painterResource(currentRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 상단 아이콘 (좌: 저장, 우: 좋아요)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* 저장 클릭 */ }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_download),
                        contentDescription = "저장",
                        tint = MainColor.copy(alpha = 0.8f)
                    )
                }
                IconButton(onClick = { /* 좋아요 클릭 */ }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_favorite),
                        contentDescription = "좋아요",
                        tint = LightRed
                    )
                }
            }

            // 시간·날짜·요일
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 72.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = now.format(timeFmt),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    Text(
                        text = now.format(dateFmt),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Spacer(Modifier.width(4.dp))
                    val fmtSec = now.format(dayFmt)
                    Text(
                        text = "($fmtSec)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
            // 하단 행동 버튼 (좌: 도서 보기, 우: 글귀 보기)
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
                    isComplete = isComplete,
                    onSwipe = {
                        isComplete = true
                        finish()
                    },
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
                    isComplete = isComplete,
                    onSwipe = {
                        isComplete = true
                        finish()
                    },
                    backgroundColor = LightBlue,
                    paintRes = R.drawable.ic_reels
                )
            }

            // 밀어서 언락 버튼
            SwipeButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp, vertical = 48.dp),
                text = "밀어서 잠금 해제 >>",
                isComplete = isComplete,
                onSwipe = {
                    isComplete = true
                    finish()
                },
            )
        }
    }

    private fun nextDrawableRes(): Int {
        val prefs = getSharedPreferences("lock_prefs", MODE_PRIVATE)
        val first = prefs.getBoolean("use_first", true)
        prefs.edit { putBoolean("use_first", !first) }
        return if (first) R.drawable.example_glim_1 else R.drawable.example_glim_2
    }
}
