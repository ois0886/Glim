package com.example.myapplication.feature.profile.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.feature.profile.RecentArticle

@Composable
internal fun RecentArticlesSection(
    articles: List<RecentArticle>,
    onViewAllClick: () -> Unit,
    onArticleLikeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profile_recent_articles),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Row(
                modifier = Modifier.clickable { onViewAllClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.profile_view_all),
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articles) { article ->
                ArticleCard(
                    article = article,
                    onLikeClick = { onArticleLikeClick(article.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRecentArticlesSection() {
    val mockArticles = listOf(
        RecentArticle(
            id = "1",
            title = "이젠 더이상 뒤돌지도 않아. 왜지, 왜 나는 이렇게 말라가는 거지.",
            timestamp = "P.51",
            likeCount = 1247,
            isLiked = false
        ),
        RecentArticle(
            id = "2",
            title = "이젠 더이상 뒤돌지도 않아. 왜지, 왜 나는 이렇게 말라가는 거지.",
            timestamp = "P.51",
            likeCount = 856,
            isLiked = true
        ),
        RecentArticle(
            id = "3",
            title = "새로운 시작은 언제나 두렵지만, 그래도 나아가야 한다.",
            timestamp = "P.42",
            likeCount = 523,
            isLiked = false
        )
    )

    MaterialTheme {
        RecentArticlesSection(
            articles = mockArticles,
            onViewAllClick = {},
            onArticleLikeClick = {}
        )
    }
}