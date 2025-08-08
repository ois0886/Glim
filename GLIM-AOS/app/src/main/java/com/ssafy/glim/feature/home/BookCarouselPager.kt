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
import coil.request.ImageRequest
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.ui.GlimErrorLoader
import com.ssafy.glim.core.ui.GlimLoader
import com.ssafy.glim.ui.theme.GlimColor.navy
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
    val cardWidth = 180.dp
    val sidePadding = (configuration.screenWidthDp.dp - cardWidth) / 2

    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    val scope = rememberCoroutineScope()
    
    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .height(272.dp),
        pageSpacing = 16.dp,
        contentPadding = PaddingValues(horizontal = sidePadding),
        pageSize = PageSize.Fixed(cardWidth)
    ) { page ->
        val book = books[page % books.size]
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

        BookPagerCard(
            book = book,
            pageOffset = pageOffset,
            onItemClick = { bookId ->
                // 더 안전한 조건 사용
                if (pagerState.currentPage == page) {
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
    val absOffset = abs(pageOffset).coerceIn(0f, 1f)
    val scale = lerp(0.6f, 1f, 1f - absOffset)
    val alpha = lerp(0.4f, 1f, 1f - absOffset)

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
            BookCoverImage(
                imageUrl = book.cover,
                title = book.title,
                modifier = Modifier.fillMaxSize()
            )

            BookInfo(
                title = book.title,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun BookCoverImage(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = title,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        loading = {
            GlimLoader()
        },
        error = {
            GlimErrorLoader()
        }
    )
}

@Composable
private fun BookInfo(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        color = Color.White,
        modifier = modifier
            .background(navy.copy(alpha = 0.8f))
            .padding(16.dp)
            .fillMaxWidth()
    )
}
