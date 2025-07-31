package com.ssafy.glim.core.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.ssafy.glim.core.data.datasource.local.QuoteLocalDataSource
import com.ssafy.glim.core.data.datasource.remote.QuoteRemoteDataSource
import com.ssafy.glim.core.data.dto.request.QuoteRequest
import com.ssafy.glim.core.data.dto.request.toRequestDto
import com.ssafy.glim.core.data.extensions.toImagePart
import com.ssafy.glim.core.data.extensions.toJsonRequestBody
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.model.QuoteSearchResult
import com.ssafy.glim.core.domain.repository.QuoteRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class QuoteRepositoryImpl @Inject constructor(
    private val quoteRemoteDataSource: QuoteRemoteDataSource,
    private val quoteLocalDataSource: QuoteLocalDataSource
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

    override suspend fun searchQuotes(query: String, page: Int, size: Int): QuoteSearchResult {
        val data = quoteRemoteDataSource.searchQuotes(query, page, size).toDomain()
        Log.d("LibraryViewModel lik2", "${data.quoteSummaries.map{it.isLiked}}")
        return data
    }


    override suspend fun getQuotes(
        page: Int,
        size: Int,
        sort: String
    ) = runCatching {
        quoteRemoteDataSource.getQuotes(page, size, sort)
            .map { it.toDomain() }
    }.onSuccess {
        quoteLocalDataSource.addQuotes(it)
    }.getOrThrow()

    override suspend fun updateQuoteViewCount(quoteId: Long) =
        quoteRemoteDataSource.updateQuoteViewCount(quoteId)

    override suspend fun getQuoteByIsbn(isbn: String) =
        quoteRemoteDataSource.getQuoteByIsbn(isbn).map { it.toDomain() }

    override suspend fun getQuoteById(quoteId: Long) =
        quoteLocalDataSource.getQuote(quoteId)

    override suspend fun likeQuote(quoteId: Long) =
        quoteRemoteDataSource.likeQuote(quoteId)

    override suspend fun unLikeQuote(quoteId: Long) =
        quoteRemoteDataSource.unLikeQuote(quoteId)

    private fun createQuoteMultipartData(
        bitmap: Bitmap
    ): MultipartBody.Part? = bitmap.toImagePart(
        partName = "quoteImage",
        fileName = "quote.jpg",
        quality = 85
    )
}
