package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.response.GlimResponse
import kotlinx.coroutines.flow.Flow

interface QuoteRemoteDataSource{
    fun fetchQuotes(page: Int, size: Int, sort: String) : Flow<List<GlimResponse>>
}