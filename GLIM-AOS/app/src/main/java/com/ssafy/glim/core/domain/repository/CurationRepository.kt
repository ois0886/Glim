package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Curation

interface CurationRepository {
    suspend fun getMainCurations(): List<Curation>
}