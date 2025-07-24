package com.ssafy.glim.feature.library.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.SearchItem

@Composable
fun PopularSearchSection(
    queries: List<SearchItem>,
    onClick: (String) -> Unit,
) {
    SearchWordListSection(
        stringResource(R.string.popular_search_query),
        queries,
        onClick
    )
}

@Composable
fun RecentSearchSection(
    queries: List<SearchItem>,
    onClick: (String) -> Unit,
    onDeleteClick: (SearchItem) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(queries) { item ->
            Row(
                modifier = Modifier
                    .background(
                        Color.LightGray.copy(alpha = 0.3f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.labelLarge,
                    modifier =
                    Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable { onClick(item.text) },
                    color = Color.Black,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_cancel),
                    contentDescription = null,
                    modifier = Modifier.clickable { onDeleteClick(item) }
                )
            }
        }
    }
}

@Composable
private fun SearchWordListSection(
    title: String,
    searchWordList: List<SearchItem> = emptyList(),
    onClick: (String) -> Unit,
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(searchWordList) { item ->
                SearchItemRow(
                    item = item,
                    onItemClick = {
                        onClick(item.text)
                    },
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun SearchItemRow(
    item: SearchItem,
    onItemClick: () -> Unit,
) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.rankStatus.symbol,
            style = MaterialTheme.typography.labelSmall,
            color = item.rankStatus.color,
        )

        Text(
            text = item.rank.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )

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
    }
    Spacer(Modifier.height(8.dp))
    HorizontalDivider(
        modifier = Modifier.height(1.dp),
        thickness = 0.5.dp,
        color = Color.LightGray.copy(alpha = 0.5f),
    )
}
