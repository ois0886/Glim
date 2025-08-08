package com.ssafy.glim.core.data.api

import com.ssafy.glim.core.data.dto.response.CurationItemResponse
import retrofit2.http.GET

/*
*   Home - 큐레이션 조회 API
* */
interface CurationApi {
    @GET("api/v1/curations/main")
    suspend fun getMainCurations(): List<CurationItemResponse>
}
