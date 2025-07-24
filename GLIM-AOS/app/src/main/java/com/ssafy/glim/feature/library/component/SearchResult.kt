package com.ssafy.glim.feature.library.component

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote

enum class SearchTab(val displayName: String) {
    BOOKS("도서"),
    QUOTES("글귀")
}

@Composable
fun SearchResultSection(
    searchQuery: String,
    bookList: List<Book>,
    quoteList: List<Quote>,
    modifier: Modifier = Modifier,
    onBookClick: (Book) -> Unit = {},
    onQuoteClick: (Quote) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(SearchTab.BOOKS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 탭 메뉴
//        TabRow(
//            selectedTabIndex = selectedTab.ordinal,
//            modifier = Modifier.padding(horizontal = 20.dp),
//            containerColor = Color.White,
//            contentColor = Color.Black,
//            indicator = { tabPositions ->
//                SecondaryIndicator(
//                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
//                    height = 2.dp,
//                    color = Color.Black
//                )
//            },
//            divider = {}
//        ) {
//            SearchTab.entries.forEach { tab ->
//                Tab(
//                    selected = selectedTab == tab,
//                    onClick = { selectedTab = tab },
//                    text = {
//                        Text(
//                            text = tab.displayName,
//                            style = MaterialTheme.typography.bodyLarge,
//                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
//                            color = Color.Black
//                        )
//                    }
//                )
//            }
//        }
//
//        // 탭 하단 구분선
//        HorizontalDivider(
//            modifier = Modifier.padding(horizontal = 20.dp),
//            thickness = 0.5.dp,
//            color = Color.LightGray
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))

        // 검색 결과 헤더
        Text(
            text = "'$searchQuery' 검색 결과 ${if (selectedTab == SearchTab.BOOKS) "${bookList.size}건" else "${quoteList.size}건"}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        // 탭별 컨텐츠
        when (selectedTab) {
            SearchTab.BOOKS -> {
                BookListContent(
                    books = bookList,
                    onBookClick = { onBookClick(it) }
                )
            }
            SearchTab.QUOTES -> {
                QuoteListContent(
                    quotes = quoteList,
                    onQuoteClick = onQuoteClick
                )
            }
        }
    }
}

@Composable
private fun BookListContent(
    books: List<Book>,
    onBookClick: (Book) -> Unit
) {
    if(books.isEmpty()) {
        NoSearchResult(
            message = stringResource(R.string.no_search_result),
            modifier = Modifier.fillMaxSize()
        )
    }

    LazyColumn(
        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books) { book ->
            BookCard(
                book = book,
                onClick = { onBookClick(book) }
            )
        }
    }
}

@Composable
private fun QuoteListContent(
    quotes: List<Quote>,
    onQuoteClick: (Quote) -> Unit
) {
    if(quotes.isEmpty()) {
        NoSearchResult(
            message = stringResource(R.string.no_search_result),
            modifier = Modifier.fillMaxSize()
        )
    }

    LazyColumn(
        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(quotes) { quote ->
            QuoteCard(
                quote = quote,
                onClick = { onQuoteClick(quote) }
            )
        }
    }
}

@Composable
private fun BookCard(
    book: Book,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 책 아이콘/이미지
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_post),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Gray
                )
            }

            // 책 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${book.author} • ${book.publisher} • ${book.pubDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // TODO: quote 텍스트 추가
            Text(
                text = "",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${quote.bookTitle} (${quote.page})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_image),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    // TODO: 좋아요 개수 추가
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun NoSearchResult(
    modifier: Modifier = Modifier,
    message: String
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}