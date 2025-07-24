package com.ssafy.glim.feature.bookdetail

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.QuoteSummary
import com.ssafy.glim.ui.theme.GlimColor.LightBrown
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlin.math.min

@Composable
fun BookDetailScreen(
    bookId: Long,
    padding: PaddingValues,
    popBackStack: () -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initBook(bookId)
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
        modifier = Modifier
            .fillMaxSize()
            .background(color = LightBrown)
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
    popBackStack: () -> Unit,
) {
    val listState = rememberLazyListState()

    val titleAlpha by animateFloatAsState(
        targetValue = when {
            listState.firstVisibleItemIndex > 0 -> 1f
            else -> min(listState.firstVisibleItemScrollOffset / 200f, 1f)
        },
        label = "titleAlpha"
    )

    Column(modifier = modifier.navigationBarsPadding()) {
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
            ) {
                item {
                    BookInfoSection(book)
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
                        TitleWithAction(title = stringResource(R.string.relative_quote))

                        for (quote in quotes) {
                            QuoteCard(quote) { onClickQuote(it) }
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
                            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis
                        )
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
                            title = stringResource(R.string.author_intro),
                            isExpanded = isAuthorDescriptionExpanded,
                            action = toggleAuthorDescriptionExpanded
                        )
                        Text(
                            text = book.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = if (isAuthorDescriptionExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            FloatingActionButton(
                onClick = openUrl,
                containerColor = LightBrown,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_forward),
                    contentDescription = stringResource(R.string.open_url),
                    tint = Color.White
                )
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
