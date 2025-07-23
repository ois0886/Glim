package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.GlimResponse
import com.ssafy.glim.core.domain.model.Glim

fun GlimResponse.toDomain(): Glim {
    return Glim(
        id = this.quoteId,
        imgUrl = this.quoteImageName,
        likes = this.quoteViews ?: 0L,
        bookTitle = this.bookTitle,
        bookAuthor = this.author,
        bookImgUrl = this.bookCoverUrl,
        isLike = false
    )
}
