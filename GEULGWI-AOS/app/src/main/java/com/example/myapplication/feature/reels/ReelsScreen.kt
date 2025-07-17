package com.example.myapplication.feature.reels

import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.core.domain.model.Glim
import com.example.myapplication.core.ui.DarkThemeScreen
import com.example.myapplication.feature.main.excludeSystemBars
import com.example.myapplication.ui.theme.GlimColor.LightGray300
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun ReelsRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    viewModel: ReelsViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    // 초기 데이터 로드
    SideEffect {
        viewModel.refresh()
    }

    // Side effects 처리
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ReelsSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is ReelsSideEffect.ShareGlim -> {
                // 공유 기능 구현
                // shareGlim(context, sideEffect.glim)
            }

            is ReelsSideEffect.ShowMoreOptions -> {
                // 더보기 옵션 표시
                // showMoreOptions(context, sideEffect.glim)
            }

            is ReelsSideEffect.CaptureSuccess -> {
                Toast.makeText(context, "이미지가 저장되었습니다: ${sideEffect.fileName}", Toast.LENGTH_SHORT)
                    .show()
            }

            is ReelsSideEffect.CaptureError -> {
                Toast.makeText(context, sideEffect.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { state.glims.size })
    val graphicsLayer = rememberGraphicsLayer()

    val captureAction =
        rememberCaptureAction(
            graphicsLayer = graphicsLayer,
            fileName = "Glim_${System.currentTimeMillis()}.jpg",
        )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.onPageChanged(page)
        }
    }

    DarkThemeScreen {
        VerticalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding.excludeSystemBars())
                    .drawWithCache {
                        onDrawWithContent {
                            graphicsLayer.record {
                                this@onDrawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        }
                    },
        ) { page ->
            val glim = state.glims[page]

            GlimItem(
                glim = glim,
                modifier = Modifier.fillMaxSize(),
                onLikeClick = { viewModel.toggleLike() },
                onShareClick = { viewModel.onShareClick() },
                onMoreClick = { },
                onCaptureClick = {
                    captureAction()
                    viewModel.onCaptureClick("Glim_${System.currentTimeMillis()}.jpg")
                },
            )
        }
    }
}

@Composable
fun GlimItem(
    glim: Glim,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onCaptureClick: () -> Unit = {},
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = glim.imgUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.example_glim_2),
            error = painterResource(R.drawable.example_glim_2),
        )

        GlimBookContent(
            modifier = Modifier.align(Alignment.BottomEnd),
            author = glim.bookAuthor,
            bookName = glim.bookTitle,
            pageInfo = glim.pageInfo.ifEmpty { "p.51" },
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
                    contentDescription = "다운로드",
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
                                if (glim.isLike) {
                                    R.drawable.ic_favorite_fill
                                } else {
                                    R.drawable.ic_favorite
                                },
                            ),
                        contentDescription = "좋아요",
                        tint = if (glim.isLike) Color.Red else Color.White,
                    )
                }
                Text(
                    "${glim.likes}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            IconButton(onClick = onShareClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = "공유",
                )
            }

            IconButton(onClick = onMoreClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = "더보기",
                )
            }
        }
    }
}

@Composable
fun GlimBookContent(
    modifier: Modifier = Modifier,
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
                            colors = listOf(Color(0x001C1B1F), Color(0xFF1C1B1F)),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY),
                        ),
                ),
        color = Color.Transparent,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .padding(end = 80.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = "", // 책 이미지 URL이 있다면 사용
                contentDescription = null,
                modifier = Modifier.size(40.dp, 56.dp),
                alpha = 0.8f,
                contentScale = ContentScale.FillHeight,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = author,
                    style = MaterialTheme.typography.labelMedium,
                    color = LightGray300,
                )
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
