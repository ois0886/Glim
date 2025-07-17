// GlimShortCardContent.kt
package com.ssafy.glim.feature.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.glim.R
import com.ssafy.glim.feature.profile.GlimShortCard

@Composable
internal fun GlimShortCardContent(
    glimCard: GlimShortCard,
    onLikeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = glimCard.title,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = glimCard.timestamp,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onLikeToggle,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = if (glimCard.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (glimCard.isLiked) {
                                stringResource(R.string.content_description_unlike)
                            } else {
                                stringResource(R.string.content_description_like)
                            },
                            tint = if (glimCard.isLiked) Color.Red else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = glimCard.likeCount.toString(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGlimShortCardContent() {
    val mockGlimCard = GlimShortCard(
        id = "1",
        title = "이젠 더이상 뒤돌지도 않아. 왜지, 왜 나는 이렇게 말라가는 거지.",
        timestamp = "P.51",
        likeCount = 1247,
        isLiked = false
    )

    MaterialTheme {
        GlimShortCardContent(
            glimCard = mockGlimCard,
            onLikeToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGlimShortCardContentLiked() {
    val mockGlimCard = GlimShortCard(
        id = "2",
        title = "이젠 더이상 뒤돌지도 않아. 왜지, 왜 나는 이렇게 말라가는 거지.",
        timestamp = "P.51",
        likeCount = 856,
        isLiked = true
    )

    MaterialTheme {
        GlimShortCardContent(
            glimCard = mockGlimCard,
            onLikeToggle = {}
        )
    }
}