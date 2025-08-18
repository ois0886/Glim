package com.example.myapplication.feature.reels

import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.myapplication.R
import com.example.myapplication.feature.main.excludeSystemBars
import com.example.myapplication.ui.theme.GlimColor.LightGray500
import kotlinx.coroutines.delay

data class Glim(
    val id: Int,
    val videoResId: Int = 1,
    val description: String = "",
    var isLike: Boolean = false,
    var likes: Int = 0,
    val user: String = "",
)

// 데이터 로딩 함수들 (실제 구현 필요)
fun loadInitialGlims(): List<Glim> {
    return (1..10).map { Glim(it, isLike = it % 2 == 0, likes = it * 2) }
}

suspend fun loadMoreGlims(currentOffset: Int): List<Glim> {
    // 추가 데이터 로드
    delay(500) // 네트워크 지연 시뮬레이션
    return (currentOffset + 1..currentOffset + 5).map { Glim(it) }
}

@Composable
internal fun ReelsRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    // 무한스크롤을 위한 상태 관리
    var glims by remember { mutableStateOf(listOf<Glim>()) }
    var isLoading by remember { mutableStateOf(false) }

    val view = LocalView.current

    LaunchedEffect(view) {
        val window = (view.context as android.app.Activity).window
        val controller = WindowCompat.getInsetsController(window, view)

        // 다크테마 적용
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        window.setFlags(
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
        )
    }

    // 초기 데이터 로드
    LaunchedEffect(Unit) {
        glims = loadInitialGlims()
    }

    val pagerState = rememberPagerState(pageCount = { glims.size })
    val graphicsLayer = rememberGraphicsLayer()

    val captureAction =
        rememberCaptureAction(
            graphicsLayer = graphicsLayer,
            fileName = "Glim_${System.currentTimeMillis()}.jpg",
        )

    // 마지막 페이지 근처에서 새 데이터 로드
    LaunchedEffect(pagerState.currentPage) {
        val currentPage = pagerState.currentPage
        val totalPages = glims.size

        // 마지막에서 2번째 아이템에 도달하면 새 데이터 로드
        if (currentPage >= totalPages - 5 && !isLoading && totalPages > 0) {
            isLoading = true
            try {
                val newGlims = loadMoreGlims(currentOffset = glims.size)
                glims = glims + newGlims
            } finally {
                isLoading = false
            }
        }
    }
    CompositionLocalProvider(LocalContentColor provides Color.White) {
        MaterialTheme(
            colorScheme = darkColorScheme(), // 다크 컬러 스킴 적용
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
        ) {
            VerticalPager(
                state = pagerState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding.excludeSystemBars())
                        .drawWithCache {
                            onDrawWithContent {
                                // 화면을 GraphicsLayer에 그리기
                                graphicsLayer.record {
                                    this@onDrawWithContent.drawContent()
                                }
                                drawLayer(graphicsLayer)
                            }
                        },
            ) { page ->

                GlimItem(
                    modifier = Modifier.fillMaxSize(),
                    isLike = glims[page].isLike,
                    likes = glims[page].likes,
                    onLikeClick = {
                        // glims 상태 업데이트 알림을 위한 새로운 리스트 생성
                        glims =
                            glims.mapIndexed { index, glim ->
                                if (index == page) {
                                    val newIsLike = !glim.isLike
                                    glim.copy(
                                        isLike = newIsLike,
                                        likes = if (newIsLike) glim.likes + 1 else glim.likes - 1,
                                    )
                                } else {
                                    glim
                                }
                            }
                    },
                    onCaptureClick = captureAction,
                )
            }
        }
    }
}

@Composable
fun GlimItem(
    modifier: Modifier,
    isLike: Boolean,
    likes: Int,
    onLikeClick: () -> Unit = {},
    onCaptureClick: () -> Unit = {},
) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.example_glim_2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        GlimBookContent(
            modifier = Modifier.align(Alignment.BottomEnd),
            "한강",
            "희랍어 시간",
            "p.51",
        )
        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .systemBarsPadding()
                    .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            IconButton(onClick = onCaptureClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        painter =
                            painterResource(
                                if (isLike) {
                                    R.drawable.ic_favorite_fill
                                } else {
                                    R.drawable.ic_favorite
                                },
                            ),
                        contentDescription = null,
                    )
                }
                Text(
                    "$likes",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = null,
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun GlimBookContent(
    modifier: Modifier,
    author: String,
    bookName: String,
    pageInfo: String,
) {
    Surface(
        modifier =
            modifier
                .background(
                    brush =
                        Brush.linearGradient(
                            colors = listOf(Color(0x001C1B1F), Color(0xFF1C1B1F)), // 시작 색과 끝 색
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY),
                        ),
                ),
        color = Color.Transparent,
    ) {
        Row(
            modifier =
                modifier
                    .padding(16.dp)
                    .padding(end = 80.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.size(40.dp, 56.dp),
                alpha = 0.8f,
                contentScale = ContentScale.FillHeight,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(text = author, style = MaterialTheme.typography.labelMedium, color = LightGray500)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$bookName ($pageInfo)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
