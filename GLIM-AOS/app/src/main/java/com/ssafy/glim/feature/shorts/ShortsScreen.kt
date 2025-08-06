package com.ssafy.glim.feature.shorts

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
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
import com.ssafy.glim.core.ui.GlimSubcomposeAsyncImage
import com.ssafy.glim.feature.main.excludeSystemBars
import com.ssafy.glim.ui.theme.GlimColor.LightGray300
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun ShortsRoute(
    quoteId: Long,
    padding: PaddingValues,
    popBackStack: () -> Unit,
    viewModel: ShortsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("ShortsRoute", "ShortsRoute SideEffect triggered with quoteId: $quoteId")
        if (quoteId >= 0) {
            viewModel.loadQuote(quoteId)
        } else {
            viewModel.refresh()
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ShortsSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is ShortsSideEffect.ShareQuote -> {
                // 공유 기능 구현
                Toast.makeText(context, "준비 중인 기능입니다.", Toast.LENGTH_SHORT).show()
            }

            is ShortsSideEffect.ShowMoreOptions -> {
                // 더보기 옵션 표시
                Toast.makeText(context, "준비 중인 기능입니다.", Toast.LENGTH_SHORT).show()
            }

            is ShortsSideEffect.CaptureSuccess -> {
                Toast.makeText(context, "이미지가 저장되었습니다: ${sideEffect.fileName}", Toast.LENGTH_SHORT)
                    .show()
            }

            is ShortsSideEffect.CaptureError -> {
                Toast.makeText(context, sideEffect.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val state by viewModel.collectAsState()

    val pagerState = rememberPagerState(pageCount = { state.quotes.size })

    LaunchedEffect(pagerState) {
        Log.d("ShortsRoute", "PagerState initialized with ${state.quotes.size} quotes")
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
        GlimSubcomposeAsyncImage(
            context = LocalContext.current,
            imageUrl = "${BuildConfig.BASE_URL}/images/${quote.quoteImageName}",
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
                }
        )

        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                modifier = Modifier.systemBarsPadding(),
                onClick = { captureAction() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = stringResource(R.string.download),
                )
            }

            Spacer(modifier = Modifier.weight(3f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        painter =
                        painterResource(
                            if (quote.isLike) {
                                R.drawable.ic_favorite_fill
                            } else {
                                R.drawable.ic_favorite
                            },
                        ),
                        contentDescription = stringResource(R.string.like),
                        tint = if (quote.isLike) Color.Red else Color.White,
                    )
                }
                Text(
                    "${quote.likes}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            IconButton(onClick = onShareClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = stringResource(R.string.share),
                )
            }

            Spacer(Modifier.weight(1f))

            QuoteBookContent(
                modifier = Modifier.fillMaxWidth(),
                bookId = quote.bookId,
                author = quote.author,
                bookName = quote.bookTitle,
                bookCover = quote.bookCoverUrl,
                page = quote.page,
            ) {
                onBookInfoClick(quote.bookId)
            }
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
        modifier = modifier,
        color = Color.DarkGray.copy(alpha = 0.8f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier =
            Modifier
                .padding(16.dp)
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
                    maxLines = 1,
                    color = LightGray300,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = bookName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
