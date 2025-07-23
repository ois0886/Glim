package com.ssafy.glim.core.service

import android.content.Context
import com.ssafy.glim.core.service.core.BaseForegroundServiceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject

class LockServiceManager
@Inject
constructor(
    @ApplicationContext val applicationContext: Context,
) : BaseForegroundServiceManager<LockService>(
    context = applicationContext,
    targetClass = LockService::class.java,
)
