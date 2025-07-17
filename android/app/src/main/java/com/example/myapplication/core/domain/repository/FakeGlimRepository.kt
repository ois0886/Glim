package com.example.myapplication.core.domain.repository

import com.example.myapplication.core.domain.model.Glim
import com.example.myapplication.core.domain.model.GlimInput
import kotlinx.coroutines.flow.Flow

interface FakeGlimRepository {
    fun getGlimData(): Flow<List<Glim>>

    fun saveGlimData(data: GlimInput): Flow<Boolean>
}
