package com.ssafy.glim.feature.post.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.feature.shorts.QuoteBookContent

@Composable
fun BookInfoSection(
    modifier: Modifier = Modifier,
    book: Book? = null,
    page: Int = 0,
    onBookInfoClick: (Long?) -> Unit,
) {
    if (book != null) {
        QuoteBookContent(
            modifier = modifier.padding(16.dp),
            bookId = book.bookId,
            author = book.author,
            bookName = book.title,
            bookCover = book.cover,
            page = page,
            onBookInfoClick = onBookInfoClick
        )
    } else {
        Surface(
            modifier = modifier.padding(16.dp),
            color = Color.DarkGray.copy(alpha = 0.8f),
            shape = RoundedCornerShape(8.dp),
        ) {
            AddBookContent(modifier, onBookInfoClick)
        }
    }
}

@Composable
private fun AddBookContent(modifier: Modifier, onBookClick: (Long?) -> Unit) {
    Row(
        modifier =
        modifier
            .padding(8.dp).padding(end = 16.dp)
            .clickable { onBookClick(null) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onBookClick(null) }) {
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
