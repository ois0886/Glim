package com.ssafy.glim.feature.search

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.feature.search.component.PopularSearchSection
import com.ssafy.glim.feature.search.component.RecentSearchSection
import com.ssafy.glim.feature.search.component.SearchResultSection
import com.ssafy.glim.feature.search.component.SearchTab
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    popBackStack: () -> Unit,
    onBookSelected: ((Book) -> Unit)? = null,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    BackHandler {
        focusManager.clearFocus()
        viewModel.onBackPressed()
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SearchSideEffect.NavigateBack -> {
                popBackStack()
            }

            is SearchSideEffect.ShowToast -> {
                Toast.makeText(
                    context,
                    sideEffect.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column(
        modifier =
        modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(padding)
    ) {
        if (state.searchMode == SearchMode.POPULAR) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 32.dp),
            ) {
                Text(
                    text = stringResource(R.string.search_book_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.search_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                )
            }
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }

        OutlinedTextField(
            value = state.currentQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            placeholder = {
                Text(
                    text = stringResource(R.string.search_hint_description),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                )
            },
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && state.searchMode == SearchMode.POPULAR) {
                        viewModel.updateSearchMode(SearchMode.RECENT)
                    }
                },
            shape = RoundedCornerShape(8.dp),
            colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
            singleLine = true,
            suffix = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    modifier =
                    Modifier.clickable {
                        viewModel.onSearchExecuted()
                    },
                    tint = Color.Black
                )
            },
            keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            keyboardActions =
            KeyboardActions(
                onSearch = {
                    viewModel.onSearchExecuted()
                },
            ),
        )

        when (state.searchMode) {
            SearchMode.POPULAR -> {
                Spacer(modifier = Modifier.height(24.dp))
                PopularSearchSection(
                    queries = state.popularSearchItems,
                    onClick = { query ->
                        viewModel.onPopularSearchItemClicked(query)
                    },
                )
            }

            SearchMode.RECENT -> {
                if (state.recentSearchItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                RecentSearchSection(
                    queries = state.recentSearchItems,
                    onClick = { query ->
                        viewModel.onRecentSearchItemClicked(query)
                    },
                    onDeleteClick = { searchItem ->
                        viewModel.onRecentSearchItemDelete(searchItem)
                    },
                )
                Spacer(modifier = Modifier.height(24.dp))
                PopularSearchSection(
                    queries = state.popularSearchItems,
                    onClick = { query ->
                        viewModel.onPopularSearchItemClicked(query)
                    },
                )
            }

            SearchMode.RESULT -> {
                Spacer(modifier = Modifier.height(8.dp))
                SearchResultSection(
                    searchQuery = state.searchQuery,
                    bookList = state.searchBooks,
                    quoteList = state.searchQuotes,
                    totalResults = state.totalResults,
                    isLoading = state.isLoading,
                    isRefreshing = state.isRefreshing,
                    selectedTab = state.selectedTab,
                    onLoadMore = {
                        if (state.selectedTab == SearchTab.BOOKS) {
                            viewModel.loadMoreBooks()
                        } else {
                            viewModel.loadMoreQuotes()
                        }
                    },
                    onBookClick = {
                        if (onBookSelected == null) {
                            viewModel.onBookClicked(it)
                        } else {
                            onBookSelected(it)
                        }
                    },
                    onQuoteClick = { viewModel.onQuoteClicked(it) },
                    selectedFilter = state.selectedFilter,
                    onSelectTab = viewModel::onSelectTab,
                    onSelectFilter = viewModel::onSelectFilter,
                )
            }
        }
    }
}
