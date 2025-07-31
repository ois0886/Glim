package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.response.QuoteResponse
import com.ssafy.glim.core.data.service.QuoteService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Query
import javax.inject.Inject

class QuoteRemoteDataSource @Inject constructor(
    private val quoteService: QuoteService
) {
    suspend fun getQuotes(
        page: Int,
        size: Int,
        sort: String
    ) = quoteService.getQuotes(
        page = page,
        size = size,
        sort = sort
    )

    suspend fun searchQuotes(
        content: String,
        page: Int,
        size: Int,
    ) = quoteService.searchQuotes(content, page, size)

    suspend fun createQuote(
        quoteData: RequestBody,
        quoteImage: MultipartBody.Part?
    ) = quoteService.createQuote(
        quoteData = quoteData,
        quoteImage = quoteImage
    )

    suspend fun updateQuoteViewCount(
        quoteId: Long
    ) = quoteService.updateQuoteViewCount(quoteId)

    suspend fun getQuoteByIsbn(
        isbn: String
    ) = quoteService.getQuoteByIsbn(isbn)

    suspend fun likeQuote(
        quoteId: Long
    ) = quoteService.likeQuote(quoteId)

    suspend fun unLikeQuote(
        quoteId: Long
    ) = quoteService.unLikeQuote(quoteId)
}
