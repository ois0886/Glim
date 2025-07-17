package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Glim
import com.ssafy.glim.core.domain.model.GlimInput
import kotlinx.coroutines.flow.Flow

interface GlimRepository {

    fun getGlimData(): Flow<List<Glim>>

    fun saveGlimData(data: GlimInput): Flow<Boolean>
}
