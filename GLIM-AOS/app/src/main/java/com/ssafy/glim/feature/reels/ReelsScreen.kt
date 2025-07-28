package com.ssafy.quote.feature.reels

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.glim.BuildConfig
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.ui.DarkThemeScreen
import com.ssafy.glim.feature.main.excludeSystemBars
import com.ssafy.glim.feature.reels.ReelsSideEffect
import com.ssafy.glim.feature.reels.ReelsViewModel
import com.ssafy.glim.feature.reels.rememberCaptureAction
import com.ssafy.glim.ui.theme.GlimColor.LightGray300
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

    SideEffect {
        viewModel.refresh()
    }

    // Side effects 처리
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ReelsSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is ReelsSideEffect.ShareQuote -> {
                // 공유 기능 구현
                Toast.makeText(context, "준비 중인 기능입니다.", Toast.LENGTH_SHORT).show()
            }

            is ReelsSideEffect.ShowMoreOptions -> {
                // 더보기 옵션 표시
                Toast.makeText(context, "준비 중인 기능입니다.", Toast.LENGTH_SHORT).show()
            }

            is ReelsSideEffect.CaptureSuccess -> {
                Toast.makeText(context, "이미지가 저장되었습니다: ${sideEffect.fileName}", Toast.LENGTH_SHORT)
                    .show()
            }

            is ReelsSideEffect.CaptureError -> {
                Toast.makeText(context, sideEffect.error, Toast.LENGTH_SHORT).show()
            }

            else -> Unit
        }
    }

    val pagerState = rememberPagerState(pageCount = { state.quotes.size })
    val graphicsLayer = rememberGraphicsLayer()

    LaunchedEffect(pagerState) {
        Log.d("ReelsRoute", "PagerState initialized with ${state.quotes.size} quotes")
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
            val quote = state.quotes[page]

            QuoteItem(
                quote = quote,
                modifier = Modifier.fillMaxSize(),
                onLikeClick = { viewModel.toggleLike() },
                onShareClick = { viewModel.onShareClick() },
                onMoreClick = { },
                onBookInfoClick = {
                    it?.let {
                        viewModel.onBookInfoClick(it)
                    }
                }
            )
        }
    }
}

@Composable
fun QuoteItem(
    quote: Quote,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onBookInfoClick: (Long?) -> Unit
) {
    val imageGraphicsLayer = rememberGraphicsLayer()

    val captureAction = rememberCaptureAction(
        graphicsLayer = imageGraphicsLayer,
        fileName = "Quote_${System.currentTimeMillis()}.jpg",
    )

    Box(modifier = modifier) {
        AsyncImage(
            model = "${BuildConfig.BASE_URL}/images/${quote.quoteImageName}",
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawWithContent {
                        // AsyncImage만 GraphicsLayer에 기록
                        imageGraphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        drawLayer(imageGraphicsLayer)
                    }
                },
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.example_glim_2),
        )


        QuoteBookContent(
            modifier = Modifier.align(Alignment.BottomEnd),
            bookId = quote.bookId,
            author = quote.author,
            bookName = quote.bookTitle,
            bookCover = quote.bookCoverUrl,
            page = quote.page,
        ) {
            onBookInfoClick(quote.bookId)
        }

        Column(
            modifier =
            Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .systemBarsPadding()
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            IconButton(onClick = {captureAction()}) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = stringResource(R.string.download),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 좋아요 기능 추가 될 시 주석 해제 및 공유 기능 구현
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                IconButton(onClick = onLikeClick) {
//                    Icon(
//                        painter =
//                        painterResource(
//                            if (quote.isLike) {
//                                R.drawable.ic_favorite_fill
//                            } else {
//                                R.drawable.ic_favorite
//                            },
//                        ),
//                        contentDescription = stringResource(R.string.like),
//                        tint = if (quote.isLike) Color.Red else Color.White,
//                    )
//                }
//                Text(
//                    "${quote.likes}",
//                    style = MaterialTheme.typography.labelMedium,
//                    fontWeight = FontWeight.Bold,
//                )
//            }
//
//            IconButton(onClick = onShareClick) {
//                Icon(
//                    painter = painterResource(R.drawable.ic_share),
//                    contentDescription = stringResource(R.string.share),
//                )
//            }
//
//            IconButton(onClick = onMoreClick) {
//                Icon(
//                    painter = painterResource(R.drawable.ic_more),
//                    contentDescription = stringResource(R.string.more),
//                )
//            }
        }
    }
}

@Composable
fun QuoteBookContent(
    modifier: Modifier = Modifier,
    bookId: Long,
    author: String,
    bookName: String,
    bookCover: String,
    page: Int,
    onBookInfoClick: (Long?) -> Unit = {},
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
                .padding(end = 80.dp)
                .clickable { onBookInfoClick(bookId) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = bookCover,
                contentDescription = null,
                modifier = Modifier.size(40.dp, 56.dp),
                alpha = 0.8f,
                contentScale = ContentScale.FillHeight,
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
                    text = "$bookName ($page)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
