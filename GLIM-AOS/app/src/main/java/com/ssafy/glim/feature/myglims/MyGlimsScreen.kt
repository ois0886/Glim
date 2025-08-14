package com.ssafy.glim.feature.myglims

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.QuoteSummary
import com.ssafy.glim.core.ui.GlimTopBar
import com.ssafy.glim.feature.main.excludeSystemBars
import com.ssafy.glim.feature.myglims.component.MyGlimsItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun MyGlimsRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit = {},
    listType: MyGlimsType = MyGlimsType.LIKED,
    viewModel: MyGlimsViewModel = hiltViewModel()
) {
    viewModel.collectSideEffect { effect ->
        when (effect) {
            is MyGlimsSideEffect.ShowToast ->
                // TODO: Toast(effect.message)
                Unit
        }
    }

    val uiState by viewModel.collectAsState()

    LaunchedEffect(listType) {
        viewModel.loadMyGlims(listType)
    }

    MyGlimsScreen(
        uiState = uiState,
        padding = padding,
        listType = listType,
        onBackClick = popBackStack,
        onNavigateToQuote = viewModel::navigateToQuote
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyGlimsScreen(
    uiState: MyGlimsUiState,
    padding: PaddingValues,
    listType: MyGlimsType,
    onBackClick: () -> Unit,
    onNavigateToQuote: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding.excludeSystemBars())
            .imePadding()
            .navigationBarsPadding()
    ) {
        GlimTopBar(
            title = listType.displayName,
            showBack = true,
            onBack = onBackClick,
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.myGlims.isEmpty()) {
            // 빈 상태 처리
            EmptyStateContent(listType = listType)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    count = uiState.myGlims.size,
                    key = { index -> uiState.myGlims[index].quoteId }
                ) { index ->
                    val quote = uiState.myGlims[index]
                    MyGlimsItem(
                        quote = quote,
                        onClick = { onNavigateToQuote(quote.quoteId) }
                    )
                }
            }
        }
    }
}

// Preview용 가짜 데이터
private val sampleQuotes = listOf(
    QuoteSummary(
        quoteId = 1,
        content = "성공은 최종 목적지가 아니라, 여행 그 자체입니다. 실패 역시 치명적이지 않습니다. 중요한 것은 계속해서 나아갈 용기를 갖는 것입니다.",
        bookTitle = "성공하는 사람들의 7가지 습관",
        page = "156",
        views = 1234,
        likes = 89,
        isLiked = true
    ),
    QuoteSummary(
        quoteId = 2,
        content = "삶이 있는 한 희망은 있다.",
        bookTitle = "희망의 철학",
        page = "23",
        views = 567,
        likes = 45,
        isLiked = false
    ),
    QuoteSummary(
        quoteId = 3,
        content = "",
        bookTitle = "빈 글귀가 있는 책",
        page = "78",
        views = 12,
        likes = 3,
        isLiked = true
    ),
    QuoteSummary(
        quoteId = 4,
        content = "지혜는 경험의 딸이다.",
        bookTitle = "",
        page = "0",
        views = 890,
        likes = 67,
        isLiked = false
    )
)

// Preview Composables
@Preview(showBackground = true, name = "글림 목록 (정상 상태)")
@Composable
private fun MyGlimsScreenPreview() {
    MaterialTheme {
        MyGlimsScreen(
            uiState = MyGlimsUiState(
                isLoading = false,
                myGlims = sampleQuotes
            ),
            padding = PaddingValues(0.dp),
            listType = MyGlimsType.LIKED,
            onBackClick = {},
            onNavigateToQuote = {}
        )
    }
}

@Preview(showBackground = true, name = "로딩 상태")
@Composable
private fun MyGlimsScreenLoadingPreview() {
    MaterialTheme {
        MyGlimsScreen(
            uiState = MyGlimsUiState(
                isLoading = true,
                myGlims = emptyList()
            ),
            padding = PaddingValues(0.dp),
            listType = MyGlimsType.LIKED,
            onBackClick = {},
            onNavigateToQuote = {}
        )
    }
}

@Preview(showBackground = true, name = "빈 상태 (좋아요)")
@Composable
private fun MyGlimsScreenEmptyLikedPreview() {
    MaterialTheme {
        MyGlimsScreen(
            uiState = MyGlimsUiState(
                isLoading = false,
                myGlims = emptyList()
            ),
            padding = PaddingValues(0.dp),
            listType = MyGlimsType.LIKED,
            onBackClick = {},
            onNavigateToQuote = {}
        )
    }
}

@Preview(showBackground = true, name = "빈 상태 (내 글림)")
@Composable
private fun MyGlimsScreenEmptyUploadedPreview() {
    MaterialTheme {
        MyGlimsScreen(
            uiState = MyGlimsUiState(
                isLoading = false,
                myGlims = emptyList()
            ),
            padding = PaddingValues(0.dp),
            listType = MyGlimsType.UPLOADED,
            onBackClick = {},
            onNavigateToQuote = {}
        )
    }
}

@Preview(showBackground = true, name = "글림 아이템 (빈 책 제목)")
@Composable
private fun MyGlimsItemEmptyTitlePreview() {
    MaterialTheme {
        MyGlimsItem(
            quote = sampleQuotes[3],
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "빈 상태 컴포넌트")
@Composable
private fun EmptyStateContentPreview() {
    MaterialTheme {
        EmptyStateContent(listType = MyGlimsType.LIKED)
    }
}

@Composable
private fun EmptyStateContent(listType: MyGlimsType) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (listType) {
                    MyGlimsType.LIKED -> stringResource(R.string.empty_liked_quotes)
                    MyGlimsType.UPLOADED -> stringResource(R.string.empty_my_quotes)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.empty_quotes_description),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
