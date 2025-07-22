package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.BookResponse
import com.ssafy.glim.core.domain.model.Book

fun BookResponse.toDomain(): Book {
    return Book(
        id = this.id,
        title = this.title,
        author = this.author,
        publisher = this.publisher,
        publishedDate = this.publishedDate,
        description = this.description,
        thumbnailUrl = this.thumbnailUrl
    )
}