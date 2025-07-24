package com.ssafy.glim.feature.post.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.quote.feature.reels.QuoteBookContent

@Composable
fun BookInfoSection(
    modifier: Modifier = Modifier,
    book: Book? = null,
    page: Int = 0,
    onBookInfoClick: (Long?) -> Unit,
) {
    if (book != null) {
        QuoteBookContent(
            modifier = modifier,
            bookId = book.itemId,
            author = book.author,
            bookName = book.title,
            page = page,
            onBookInfoClick = onBookInfoClick
        )
    } else {
        AddBookContent(modifier, onBookInfoClick)
    }
}

@Composable
private fun AddBookContent(modifier: Modifier, onBookClick: (Long?) -> Unit) {
    Row(
        modifier =
        modifier
            .padding(16.dp)
            .padding(end = 80.dp)
            .clickable { onBookClick(null) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.icon_post),
                contentDescription = null,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.add_book_info),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}
