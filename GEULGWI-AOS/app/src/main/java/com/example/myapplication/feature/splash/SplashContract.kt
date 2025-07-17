package com.example.myapplication.feature.splash

sealed interface SplashUiState {
    data object Loading : SplashUiState
}

sealed interface SplashSideEffect {
    data object NavigateToLogin : SplashSideEffect
}
