package com.ssafy.glim.core.domain.repository

interface ShortLinkRepository {
    suspend fun shortenUrl(longUrl: String): String
}
