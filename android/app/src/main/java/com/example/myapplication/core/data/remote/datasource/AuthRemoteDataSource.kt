package com.example.myapplication.core.data.remote.datasource

import com.example.myapplication.core.data.remote.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource
    @Inject
    constructor(
        service: AuthService,
    )
