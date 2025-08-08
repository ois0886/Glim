package com.ssafy.glim.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ssafy.glim.core.domain.model.Book
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun BookCarouselPager(
    books: List<Book>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (books.isEmpty()) return

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = 180.dp
    val pageSpacing = 16.dp

    // 화면 중앙에 카드가 오도록 패딩 계산
    val sidePadding = (screenWidth - cardWidth) / 2

    val pagerState = rememberPagerState(
        initialPage = Integer.MAX_VALUE / 2,
        pageCount = { Integer.MAX_VALUE }
    )

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        pageSpacing = pageSpacing,
        contentPadding = PaddingValues(horizontal = sidePadding),
        pageSize = PageSize.Fixed(cardWidth)
    ) { page ->
        val book = books[page % books.size]
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val scope = rememberCoroutineScope()

        BookPagerCard(
            book = book,
            pageOffset = pageOffset,
            onItemClick = { bookId ->
                if (pageOffset == 0f) {
                    onItemClick(bookId)
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun BookPagerCard(
    book: Book,
    pageOffset: Float,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // 페이지 오프셋에 따른 스케일, 알파, 그림자 계산
    val scale = lerp(0.6f, 1f, 1f - abs(pageOffset).coerceIn(0f, 1f))
    val alpha = lerp(0.4f, 1f, 1f - abs(pageOffset).coerceIn(0f, 1f))

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable { onItemClick(book.bookId) },
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 책 커버 이미지
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.cover)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = book.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Error",
                            modifier = Modifier.size(48.dp),
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(
                        Color(0xCC004784)
                    )
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )

                if (book.categoryText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = book.categoryText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 상단 우측 가격 정보 (선택사항)
            if (book.priceStandard > 0) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = book.priceText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
