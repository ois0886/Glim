package com.ssafy.glim.feature.bookdetail

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.QuoteSummary
import com.ssafy.glim.ui.theme.GlimColor.LightBrown
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlin.math.min

@Composable
fun BookDetailScreen(
    isbn: String?,
    bookId: Long?,
    padding: PaddingValues,
    popBackStack: () -> Unit = {},
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initBook(isbn, bookId)
    }

    viewModel.collectSideEffect {
        when (it) {
            is BookDetailSideEffect.ShowToast -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

            is BookDetailSideEffect.OpenUrl -> {
                val intent = Intent(Intent.ACTION_VIEW, it.url.toUri())
                context.startActivity(intent)
            }
        }
    }

    BookDetailContent(
        book = state.bookDetail,
        quotes = state.quoteSummaries,
        isDescriptionExpanded = state.isDescriptionExpanded,
        isAuthorDescriptionExpanded = state.isAuthorDescriptionExpanded,
        onClickQuote = viewModel::onClickQuote,
        openUrl = viewModel::openUrl,
        toggleBookDescriptionExpanded = viewModel::toggleBookDescriptionExpanded,
        toggleAuthorDescriptionExpanded = viewModel::toggleAuthorDescriptionExpanded,
        popBackStack = popBackStack,
        clickPostGlim = viewModel::clickPostGlim,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    )
}

@Composable
fun BookDetailContent(
    modifier: Modifier = Modifier,
    book: Book,
    quotes: List<QuoteSummary>,
    isDescriptionExpanded: Boolean = false,
    isAuthorDescriptionExpanded: Boolean = false,
    onClickQuote: (Long) -> Unit,
    openUrl: () -> Unit,
    toggleBookDescriptionExpanded: () -> Unit,
    toggleAuthorDescriptionExpanded: () -> Unit,
    clickPostGlim: () -> Unit,
    popBackStack: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    val pagerState = rememberPagerState(pageCount = { quotes.size })

    val titleAlpha by animateFloatAsState(
        targetValue = when {
            listState.firstVisibleItemIndex > 0 -> 1f
            else -> min(listState.firstVisibleItemScrollOffset / 200f, 1f)
        },
        label = "titleAlpha"
    )

    Column(modifier = modifier) {
        BookDetailTopBar(
            title = book.title,
            alpha = titleAlpha,
            onBackClick = popBackStack
        )
        Box {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    AsyncImage(
                        model = book.cover,
                        contentDescription = stringResource(R.string.book_cover),
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .shadow(
                                elevation = 12.dp,
                            )
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_image),
                        error = painterResource(id = R.drawable.ic_image)
                    )
                }
                item {
                    Spacer(Modifier.height(8.dp))
                    BookInfoSection(book = book, quoteCount = quotes.size)
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        thickness = 8.dp,
                        color = Color(0xFFF7F7F7)
                    )
                }
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                    ) {
                        TitleWithAction(title = stringResource(R.string.relative_quote))
                        Spacer(modifier = Modifier.height(8.dp))

                        if (quotes.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_relative_quote),
                                modifier = Modifier
                                    .padding(top = 16.dp, bottom = 8.dp)
                                    .align(Alignment.CenterHorizontally),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth(),
                            pageSpacing = 8.dp,
                            contentPadding = PaddingValues(horizontal = 16.dp),
                        ) { page ->
                            QuoteCard(
                                quote = quotes[page],
                                modifier = Modifier.fillMaxWidth(),
                                onClickCard = onClickQuote
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(
                        thickness = 8.dp,
                        color = Color(0xFFF7F7F7)
                    )
                }
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TitleWithAction(
                            title = stringResource(R.string.book_summary),
                            isExpanded = isDescriptionExpanded,
                            action = toggleBookDescriptionExpanded
                        )
                        Text(
                            text = book.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                // TODO: 작가소개 데이터 올 시 주석 해제
//                item {
//                    Spacer(modifier = Modifier.height(24.dp))
//                    HorizontalDivider(
//                        thickness = 8.dp,
//                        color = Color(0xFFF7F7F7)
//                    )
//                }
//                item {
//                    Column(
//                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        TitleWithAction(
//                            title = stringResource(R.string.author_intro),
//                            isExpanded = isAuthorDescriptionExpanded,
//                            action = toggleAuthorDescriptionExpanded
//                        )
//                        Text(
//                            text = book.description,
//                            style = MaterialTheme.typography.bodyMedium,
//                            maxLines = if (isAuthorDescriptionExpanded) Int.MAX_VALUE else 3,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    }
//                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = openUrl,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = LightBrown
                    ),
                    border = BorderStroke(1.dp, LightBrown),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "책 구매",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Button(
                    onClick = clickPostGlim,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBrown,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "글귀 등록",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TitleWithAction(
    title: String,
    isExpanded: Boolean = false,
    action: () -> Unit = {},
    content: @Composable () -> Unit = {
        IconButton(
            onClick = action
        ) {
            Icon(
                painter = painterResource(id = if (isExpanded) R.drawable.ic_arrow_down else R.drawable.ic_forward),
                contentDescription = stringResource(R.string.more),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    },
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}
