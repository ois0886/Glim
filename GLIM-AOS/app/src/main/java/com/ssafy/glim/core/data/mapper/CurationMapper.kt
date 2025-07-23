package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.CurationContentResponse
import com.ssafy.glim.core.data.dto.response.CurationItemResponse
import com.ssafy.glim.core.domain.model.Curation
import com.ssafy.glim.core.domain.model.CurationContent
import com.ssafy.glim.core.domain.model.CurationType

class CurationMapper {
    fun mapToDomain(dto: CurationItemResponse): Curation {
        return Curation(
            id = dto.curationItemId,
            title = dto.title,
            description = dto.description,
            type = CurationType.valueOf(dto.curationType),
            contents = dto.contents.map { mapContent(it) }
        )
    }

    private fun mapContent(dto: CurationContentResponse) = CurationContent(
        bookId = dto.bookId,
        title = dto.bookTitle,
        author = dto.author,
        publisher = dto.publisher,
        coverUrl = dto.bookCoverUrl,
        quoteId = dto.quoteId,
        imageName = dto.imageName
    )
}