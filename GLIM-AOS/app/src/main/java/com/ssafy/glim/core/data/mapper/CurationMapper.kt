package com.ssafy.glim.core.data.mapper

import android.util.Log
import com.ssafy.glim.core.data.dto.response.CurationContentResponse
import com.ssafy.glim.core.data.dto.response.CurationItemResponse
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Curation
import com.ssafy.glim.core.domain.model.CurationContent
import com.ssafy.glim.core.domain.model.CurationType
import com.ssafy.glim.core.domain.model.Quote

fun CurationItemResponse.toDomain(): Curation {
    val typeEnum = CurationType.valueOf(this.curationType)
    val books = if (typeEnum == CurationType.BOOK) {
        Log.d("CurationMapper", "Curation type is BOOK, converting contents to Book ${contents.get(0)}")
        this.contents.map { it.toBook() }
    } else {
        emptyList()
    }

    val glims = if (typeEnum == CurationType.QUOTE) {
        this.contents.map { it.toQuote() }
    } else {
        emptyList()
    }

    return Curation(
        id = this.curationItemId,
        title = this.title,
        description = this.description,
        type = typeEnum,
        contents = CurationContent(
            book = books,
            quote = glims
        )
    )
}

private fun CurationContentResponse.toBook(): Book =
    Book(
        bookId = this.bookId ?: -1L,
        title = this.bookTitle,
        author = this.author,
        publisher = this.publisher,
        pubDate = "",
        isbn = "",
        description = "",
        cover = this.bookCoverUrl ?: "",
    )

private fun CurationContentResponse.toQuote(): Quote =
    Quote(
        bookId = bookId ?: -1L,
        isLike = false,
        likes = 0L,
        bookTitle = bookTitle,
        author = author,
        bookCoverUrl = bookCoverUrl ?: "",
        page = 0,
        publisher = publisher,
        quoteId = quoteId ?: -1L,
        quoteImageName = imageName ?: "",
        quoteViews = 0L,
    )
