package com.ssafy.glim.feature.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ssafy.glim.BuildConfig
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.ui.GlimErrorLoader
import com.ssafy.glim.core.ui.GlimLoader
import com.ssafy.glim.core.ui.GlimSubcomposeAsyncImage
import com.ssafy.glim.feature.home.model.HomeSectionUiModel
import com.ssafy.glim.ui.theme.GlimColor.LightGray600
import com.ssafy.glim.ui.theme.GlimColor.LightGray700
import com.ssafy.glim.ui.theme.GlimColor.LightGray900
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun HomeRoute(
    padding: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val homeUiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is HomeSideEffect.ShowError -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    HomeScreen(
        padding = padding,
        homeUiState = homeUiState,
        onQuoteClick = viewModel::navigateToQuote,
        onBookClick = viewModel::navigateToBookDetail,
    )
}

@Composable
private fun HomeScreen(
    padding: PaddingValues,
    homeUiState: HomeUiState,
    onQuoteClick: (Long) -> Unit,
    onBookClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        item {
            QuoteHomeTitle()
        }
        items(homeUiState.sections) { section ->
            when (section) {
                is HomeSectionUiModel.QuoteSection -> {
                    SectionTitle(section.title)
                    QuoteCarousel(
                        quotes = section.quotes,
                        onItemClick = onQuoteClick,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 16.dp),
                        thickness = 8.dp,
                        color = Color(0xFFF7F7F7)
                    )
                }

                is HomeSectionUiModel.BookSection -> {
                    SectionTitle(section.title)
                    BookCarousel(
                        books = section.books,
                        onItemClick = onBookClick,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 16.dp),
                        thickness = 8.dp,
                        color = Color(0xFFF7F7F7)
                    )
                }
            }
        }
    }
}

@Composable
fun QuoteHomeTitle() {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.today_glim),
            style =
            MaterialTheme.typography.headlineMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.glim_home_description),
            style =
            MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
            ),
            color = LightGray600,
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style =
        MaterialTheme.typography.titleMedium.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        ),
        modifier = Modifier.padding(16.dp),
    )
}

@Composable
fun QuoteCarousel(
    quotes: List<Quote>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    itemSize: DpSize = DpSize(width = 240.dp, height = 360.dp),
    itemSpacing: Dp = 12.dp
) {
    val context = LocalContext.current
    val imageLoader = context.imageLoader
    LaunchedEffect(quotes) {
        quotes.forEach { quote ->
            val request = ImageRequest.Builder(context)
                .data(quote.quoteImageName)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            imageLoader.enqueue(request)
        }
    }
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
    ) {
        items(quotes) { quote ->
            Column(
                modifier =
                Modifier
                    .width(itemSize.width)
                    .clickable { onItemClick(quote.quoteId) },
                horizontalAlignment = Alignment.Start,
            ) {
                Card(
                    modifier = Modifier.size(itemSize),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    GlimSubcomposeAsyncImage(
                        context = context,
                        imageUrl = "${BuildConfig.BASE_URL}/images/${quote.quoteImageName}",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = quote.bookTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightGray900,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.weight(6F),
                        text = quote.author,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = LightGray700,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1F))
                }
            }
        }
    }
}

@Composable
fun BookCarousel(
    books: List<Book>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    itemWidth: Dp = 80.dp,
    itemSpacing: Dp = 12.dp,
) {
    val context = LocalContext.current
    val imageLoader = context.imageLoader
    LaunchedEffect(books) {
        books.forEach { book ->
            val request = ImageRequest.Builder(context)
                .data(book.cover)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            imageLoader.enqueue(request)
        }
    }

    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
    ) {
        items(books) { book ->
            Column(
                modifier =
                Modifier
                    .width(itemWidth)
                    .clickable { onItemClick(book.bookId) },
                horizontalAlignment = Alignment.Start
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.size(width = itemWidth, height = 120.dp),
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(book.cover)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        imageLoader = imageLoader,
                        loading = {
                            GlimLoader(Modifier)
                        },
                        error = {
                            GlimErrorLoader(Modifier)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.title,
                    style =
                    MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
