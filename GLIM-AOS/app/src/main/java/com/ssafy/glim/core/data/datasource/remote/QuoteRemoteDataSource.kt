package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.response.QuoteResponse
import com.ssafy.glim.core.data.service.QuoteService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class QuoteRemoteDataSource @Inject constructor(
    private val quoteService: QuoteService
) {
    suspend fun getQuotes(
        page: Int,
        size: Int = 10,
        sort: String
    ): List<QuoteResponse> = quoteService.getQuotes(
        page = page,
        size = size,
        sort = sort
    )

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
}