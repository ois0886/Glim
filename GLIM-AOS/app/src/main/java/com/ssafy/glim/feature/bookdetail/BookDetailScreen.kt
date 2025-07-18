package com.ssafy.glim.feature.bookdetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Quote
import org.orbitmvi.orbit.compose.collectAsState
import kotlin.text.Typography.quote

@Composable
fun BookDetailScreen(
    padding: PaddingValues,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    BookDetailContent(
        state = state.bookDetail,
        onClickQuote = viewModel::onClickQuote,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color.White),
    )
}

@Composable
fun BookDetailContent(
    state: BookDetail,
    onClickQuote: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                    )
                }
            }

        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth().background(Color.Transparent)
            ) {
                FloatingActionButton(
                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_forward),
                        contentDescription = "Add Quote",
                        tint = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->

        LazyColumn(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            item {
                BookInfoSection(state)
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
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TitleWithAction(title = "관련 글귀")

                    for (quote in state.quotes) {
                        QuoteCard(quote)
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
                    TitleWithAction("도서 개요")
                    Text(
                        text = state.description,
                        style = MaterialTheme.typography.bodyMedium,
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
                if(state.authorDescription != null) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TitleWithAction("작가 소개")
                        Text(
                            text = state.authorDescription,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }

        }
    }
}

@Composable
private fun TitleWithAction(
    title: String,
    content: @Composable () -> Unit = {
        IconButton(
            onClick = {}
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_forward),
                contentDescription = "Add Quote",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
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

@Composable
private fun QuoteCard(quote: Quote) {
    Card(
        modifier = Modifier
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = quote.text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = quote.page.toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = quote.likes.toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun BookInfoSection(state: BookDetail) {
    Row {
        AsyncImage(
            model = state.coverImageUrl,
            contentDescription = "Book Cover",
            modifier = Modifier
                .size(80.dp, 120.dp)
                .padding(16.dp),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_image),
            error = painterResource(id = R.drawable.ic_image)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = state.subTitle,
                style = MaterialTheme.typography.labelMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book title
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = state.category,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book author
                Text(
                    text = state.author,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = state.publicationDate,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book publisher
                Text(
                    text = state.publisher,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    text = "정가 ${state.price} 원",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview
@Composable
fun BookDetailScreenPreview() {
    BookDetailContent(
        state = BookDetail(
            title = "Sample Book Title",
            subTitle = "Sample Subtitle",
            author = "Author Name",
            publisher = "Publisher Name",
            publicationDate = "2023-10-01",
            category = "Fiction",
            price = 15000,
            coverImageUrl = "https://example.com/cover.jpg"
        ),
        onClickQuote = {}
    )
}