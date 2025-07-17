package com.example.myapplication.feature.library

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun LibraryRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    onSearchQueryChange: (String) -> Unit = {},
    onAuthorClick: (String) -> Unit = {},
    onBookClick: (String) -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
    ) {
        // 헤더
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 32.dp),
        ) {
            Text(
                text = "도서 검색",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "글귀, 책제목, 작가이름으로 찾아보세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }

        // 검색창
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                onSearchQueryChange(it)
            },
            placeholder = {
                Text(
                    text = "도서명, 작가, 글귀 검색어를 입력해 주세요.",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                )
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 인기 검색어 섹션
//        PopularSearchSection(onAuthorClick, onBookClick)
        RecentSearchSection(onAuthorClick, onBookClick)
    }
}

@Composable
private fun PopularSearchSection(
    onAuthorClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
) {
    SearchWordListSection("인기 검색어", false, onAuthorClick, onBookClick)
}

@Composable
private fun RecentSearchSection(
    onAuthorClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
) {
    SearchWordListSection("최근 검색 모록", true, onAuthorClick, onBookClick)
}

@Composable
private fun SearchWordListSection(
    title: String,
    isDeleteAble: Boolean = false,
    onAuthorClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 검색어 목록
        val popularSearches =
            listOf(
                SearchItem("희랍어 시간", "책제목"),
                SearchItem("한강", "작가"),
                SearchItem("박성준", "작가"),
                SearchItem("박승준", "작가"),
                SearchItem("폴리노", "글귀"),
                SearchItem("잠오도", "글귀"),
                SearchItem("한강", "작가"),
            )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(popularSearches) { item ->
                SearchItemRow(
                    item = item,
                    isDeleteAble = isDeleteAble,
                    onItemClick = {
                        when (item.type) {
                            "작가" -> onAuthorClick(item.text)
                            "책제목" -> onBookClick(item.text)
                            else -> {}
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun SearchItemRow(
    item: SearchItem,
    isDeleteAble: Boolean,
    onItemClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onItemClick() }
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = item.type,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier =
                Modifier
                    .background(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
        )

        if (isDeleteAble) {
            IconButton(onClick = {}) {
                Icon(painter = painterResource(R.drawable.icon_post), contentDescription = null)
            }
        }
    }
    HorizontalDivider(
        modifier = Modifier.height(1.dp),
        thickness = 0.5.dp,
        color = Color.LightGray.copy(alpha = 0.5f),
    )
}

data class SearchItem(
    val text: String,
    val type: String,
)
