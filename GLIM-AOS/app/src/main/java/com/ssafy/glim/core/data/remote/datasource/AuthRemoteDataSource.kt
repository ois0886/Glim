package com.ssafy.glim.core.data.remote.datasource

import com.ssafy.glim.core.data.remote.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource
    @Inject
    constructor(
        service: AuthService,
    )
