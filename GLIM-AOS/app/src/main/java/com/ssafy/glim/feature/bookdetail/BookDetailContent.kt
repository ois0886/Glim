package com.ssafy.glim.feature.bookdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.QuoteSummary
import com.ssafy.glim.ui.theme.GlimColor.LightBrown

@Composable
fun BookDetailTopBar(
    title: String,
    alpha: Float,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightBrown),
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
fun BookInfoSection(book: Book) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush =
                    Brush.linearGradient(
                        colors = listOf(LightBrown, LightBrown.copy(alpha = 0.0f)),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY),
                    ),
            )
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        AsyncImage(
            model = book.cover,
            contentDescription = stringResource(R.string.book_cover),
            modifier = Modifier
                .size(80.dp, 120.dp),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_image),
            error = painterResource(id = R.drawable.ic_image)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = book.categoryText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = stringResource(R.string.publication_date) + " ${book.pubDate}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.publisher,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = stringResource(R.string.price) + " ${book.priceText}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuoteCard(quote: QuoteSummary, onClickCard: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClickCard(quote.quoteId) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = quote.content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = quote.page.toString(),
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = stringResource(R.string.like),
                modifier = Modifier.padding(end = 4.dp)
            )

            Text(
                text = quote.views.toString(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
