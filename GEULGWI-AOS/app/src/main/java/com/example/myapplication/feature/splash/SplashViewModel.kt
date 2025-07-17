package com.example.myapplication.feature.splash

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class SplashViewModel
    @Inject
    constructor() :
    ViewModel(), ContainerHost<SplashUiState, SplashSideEffect> {
        override val container =
            container<SplashUiState, SplashSideEffect>(
                initialState = SplashUiState.Loading,
            ).apply {
                intent {
                    delay(1500)
                    postSideEffect(SplashSideEffect.NavigateToLogin)
                }
            }
    }
