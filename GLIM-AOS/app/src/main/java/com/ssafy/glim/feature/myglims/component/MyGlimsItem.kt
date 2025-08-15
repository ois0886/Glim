package com.ssafy.glim.feature.myglims.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.QuoteSummary
import kotlin.text.ifEmpty

@Composable
fun MyGlimsItem(
    quote: QuoteSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 글림 내용 또는 빈 상태 메시지
            if (quote.content.isNotEmpty()) {
                Text(
                    text = quote.content,
                    fontSize = 16.sp,
                    color = Color.Black,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.no_quote_content),
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 책 정보 - 가중치를 주어 공간 확보
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = quote.bookTitle.ifEmpty {
                            stringResource(R.string.unknown_book_title)
                        },
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (quote.page.toLong() > 0) {
                        Text(
                            text = stringResource(R.string.page_format, quote.page),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // 책 정보와 통계 사이 여백 추가
                Spacer(modifier = Modifier.width(16.dp))

                // 조회수와 좋아요 수
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = stringResource(R.string.views),
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = quote.views.toString(),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (quote.isLiked) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = stringResource(R.string.likes),
                            modifier = Modifier.size(14.dp),
                            tint = if (quote.isLiked) {
                                Color.Red
                            } else {
                                Color.Gray
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = quote.likes.toString(),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
