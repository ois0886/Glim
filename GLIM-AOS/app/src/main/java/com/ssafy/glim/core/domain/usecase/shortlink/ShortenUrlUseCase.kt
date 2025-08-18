package com.ssafy.glim.core.domain.usecase.shortlink

import com.ssafy.glim.core.domain.repository.ShortLinkRepository
import javax.inject.Inject

class ShortenUrlUseCase @Inject constructor(
    private val shortLinkRepository: ShortLinkRepository
) {
    suspend operator fun invoke(longUrl: String) = shortLinkRepository.shortenUrl(longUrl)
}
