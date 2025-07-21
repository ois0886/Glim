package com.ssafy.glim.feature.post.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.feature.reels.GlimBookContent

@Composable
fun BookInfoSection(
    modifier: Modifier = Modifier,
    book: Book? = null,
    page: String = "",
    onBookClick: () -> Unit
) {
    if (book != null) {
        GlimBookContent(
            modifier = modifier,
            author = book.author,
            bookName = book.title,
            pageInfo = page
        )
    }
    else {
        AddBookContent(modifier, onBookClick)
    }
}

@Composable
private fun BookInfoContent(
    modifier: Modifier,
    onBookClick: () -> Unit,
    book: Book
) {
    Row(
        modifier =
            modifier
                .padding(16.dp)
                .padding(end = 80.dp)
                .clickable { onBookClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = book.coverImageUrl,
            contentDescription = null,
            modifier = Modifier.size(width = 40.dp, height = 60.dp),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.height(4.dp))
        Column {
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = book.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }

    }
}

@Composable
private fun AddBookContent(modifier: Modifier, onBookClick: () -> Unit) {
    Row(
        modifier =
            modifier
                .padding(16.dp)
                .padding(end = 80.dp)
                .clickable { onBookClick() },
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
