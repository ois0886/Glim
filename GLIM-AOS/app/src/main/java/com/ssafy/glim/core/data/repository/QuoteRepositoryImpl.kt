package com.ssafy.glim.core.data.repository

import android.graphics.Bitmap
import com.ssafy.glim.core.data.datasource.remote.QuoteRemoteDataSource
import com.ssafy.glim.core.data.dto.request.QuoteRequest
import com.ssafy.glim.core.data.dto.request.toRequestDto
import com.ssafy.glim.core.data.extensions.toImagePart
import com.ssafy.glim.core.data.extensions.toJsonRequestBody
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.repository.QuoteRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class QuoteRepositoryImpl @Inject constructor(
    private val quoteRemoteDataSource: QuoteRemoteDataSource,
) : QuoteRepository {

    override suspend fun createQuote(
        content: String,
        isbn: String,
        book: Book,
        bitmap: Bitmap
    ) {
        val quoteRequest = QuoteRequest(
            content = content,
            isbn = isbn,
            bookCreateData = book.toRequestDto()
        )
        val jsonRequestBody = quoteRequest.toJsonRequestBody()
        val image = createQuoteMultipartData(bitmap)

        quoteRemoteDataSource.createQuote(jsonRequestBody, image)
    }

    override fun searchQuotes(query: String): List<Quote> {
        TODO("Not yet implemented")
    }

    override suspend fun getQuotes(page: Int, size: Int, sort: String) =
        quoteRemoteDataSource.getQuotes(page, size, sort)
            .map { it.toDomain() }

    override suspend fun updateQuoteViewCount(quoteId: Long) =
        quoteRemoteDataSource.updateQuoteViewCount(quoteId)

    private fun createQuoteMultipartData(
        bitmap: Bitmap
    ): MultipartBody.Part? = bitmap.toImagePart(
        partName = "quoteImage",
        fileName = "quote.jpg",
        quality = 85
    )
}


