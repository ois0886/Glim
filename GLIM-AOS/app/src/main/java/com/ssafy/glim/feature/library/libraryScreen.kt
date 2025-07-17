package com.ssafy.glim.feature.library

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.feature.library.component.PopularSearchSection
import com.ssafy.glim.feature.library.component.RecentSearchSection
import com.ssafy.glim.feature.library.component.SearchResultSection

enum class SearchMode {
    POPULAR,
    RECENT,
    RESULT
}

@Composable
fun LibraryRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchMode by remember { mutableStateOf(SearchMode.POPULAR) }
    val focusManager = LocalFocusManager.current

    // 뒤로가기 처리
    BackHandler(enabled = searchMode != SearchMode.POPULAR) {
        when(searchMode) {
            SearchMode.RECENT -> {
                focusManager.clearFocus()
                searchMode = SearchMode.POPULAR
            }
            SearchMode.RESULT -> {
                searchQuery = ""
                searchMode = SearchMode.RECENT
            }
            SearchMode.POPULAR -> Unit
        }

    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
    ) {
        // 헤더 - 인기 검색어 모드일 때만 표시
        if (searchMode == SearchMode.POPULAR) {
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
        } else {
            // 최근 검색어 모드일 때 상단 여백 추가
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 검색창
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
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
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .onFocusChanged { focusState ->
                        // 포커스를 받으면 최근 검색어 모드로 변경
                        if (focusState.isFocused && searchMode == SearchMode.POPULAR) {
                            searchMode = SearchMode.RECENT
                        }
                    },
            shape = RoundedCornerShape(8.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
            singleLine = true,
            suffix = {
                Icon(painter = painterResource(R.drawable.ic_search), contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // 검색어 입력 후 엔터키를 누르면 검색 실행
                    searchMode = SearchMode.RESULT
                }
            )
        )

        // 모드에 따라 다른 섹션 표시
        when (searchMode) {
            SearchMode.POPULAR -> {
                Spacer(modifier = Modifier.height(24.dp))
                PopularSearchSection {
                    searchQuery = it
                    searchMode = SearchMode.RESULT
                }
            }
            SearchMode.RECENT -> {
                Spacer(modifier = Modifier.height(24.dp))
                RecentSearchSection {
                    searchQuery = it
                    searchMode = SearchMode.RESULT
                }
            }
            SearchMode.RESULT -> {
                Spacer(modifier = Modifier.height(8.dp))
                SearchResultSection(
                    searchQuery = searchQuery,
                )
            }
        }
    }
}