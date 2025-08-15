package com.ssafy.glim.core.data.repository

import com.ssafy.glim.BuildConfig
import com.ssafy.glim.core.data.api.ShortLinkApi
import com.ssafy.glim.core.data.api.ShortenUrlRequest
import com.ssafy.glim.core.domain.repository.ShortLinkRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShortLinkRepositoryImpl @Inject constructor(
    private val shortLinkApi: ShortLinkApi
) : ShortLinkRepository {
    
    companion object {
        private const val AUTHORIZATION_PREFIX = "Bearer "
    }
    
    override suspend fun shortenUrl(longUrl: String) = shortLinkApi.shortenUrl(
                authorization = AUTHORIZATION_PREFIX + BuildConfig.BITLY_TOKEN,
                request = ShortenUrlRequest(longUrl = longUrl)
            ).link

}
