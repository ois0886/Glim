package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.CurationContentResponse
import com.ssafy.glim.core.data.dto.response.CurationItemResponse
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Curation
import com.ssafy.glim.core.domain.model.CurationContent
import com.ssafy.glim.core.domain.model.CurationType
import com.ssafy.glim.core.domain.model.Glim

fun CurationItemResponse.toDomain(): Curation {
    val typeEnum = CurationType.valueOf(this.curationType)
    val books = if (typeEnum == CurationType.BOOK) {
        this.contents.map { it.toBook() }
    } else emptyList()

    val glims = if (typeEnum == CurationType.QUOTE) {
        this.contents.map { it.toGlim() }
    } else emptyList()

    return Curation(
        id = this.curationItemId,
        title = this.title,
        description = this.description,
        type = typeEnum,
        contents = CurationContent(
            book = books,
            glim = glims
        )
    )
}

private fun CurationContentResponse.toBook(): Book =
    Book(
        id = this.bookId ?: -1L,
        title = this.bookTitle,
        author = this.author,
        publisher = this.publisher,
        publicationDate = "",
        isbn = "",
        description = "",
        coverImageUrl = this.bookCoverUrl
    )

private fun CurationContentResponse.toGlim(): Glim =
    Glim(
        id = this.quoteId ?: -1L,
        bookId = this.bookId ?: -1L,
        imgUrl = this.imageName.orEmpty(),
        isLike = false,
        likes = 0L,
        bookTitle = this.bookTitle,
        bookAuthor = this.author,
        bookImgUrl = this.bookCoverUrl.orEmpty(),
        pageInfo = ""
    )

