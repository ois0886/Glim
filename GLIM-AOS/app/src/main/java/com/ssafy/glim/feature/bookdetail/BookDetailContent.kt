package com.ssafy.glim.feature.bookdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.QuoteSummary

@Composable
fun BookDetailTopBar(
    title: String,
    alpha: Float,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(R.string.back),
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .alpha(alpha)
                .weight(1f)
                .padding(end = 16.dp)
        )
    }
}

@Composable
fun BookInfoSection(modifier: Modifier = Modifier, book: Book) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = book.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = book.categoryText,
            style = MaterialTheme.typography.bodySmall,
        )

        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = book.publisher,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f),
                fontWeight = FontWeight.Bold
            )

            Text(
                text = book.pubDate,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.price) + " ${book.priceText}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun QuoteCard(
    quote: QuoteSummary,
    modifier: Modifier,
    onClickCard: (Long) -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClickCard(quote.quoteId) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                painter = painterResource(R.drawable.ic_quote),
                contentDescription = null
            )
            Text(
                text = quote.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                minLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_views),
                    contentDescription = stringResource(R.string.views),
                    modifier = Modifier.padding(end = 2.dp)
                )
                Text(
                    text = quote.views.toString(),
                    style = MaterialTheme.typography.labelLarge,
                )

                Spacer(Modifier.weight(1f))

                    Icon(
                        painter = painterResource(
                            if (quote.isLiked) {
                                R.drawable.ic_like_200_fill
                            } else {
                                R.drawable.ic_like_200
                            }
                        ),
                        contentDescription = null,
                        tint =
                            if (quote.isLiked) {
                                Color.Red
                            } else {
                                Color.Black
                            },
                    )
                    Text(
                        text = quote.likes.toString(),
                        style = MaterialTheme.typography.labelLarge,
                    )

            }
        }
    }
}
