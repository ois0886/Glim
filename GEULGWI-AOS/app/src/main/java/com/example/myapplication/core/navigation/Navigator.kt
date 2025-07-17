package com.example.myapplication.core.navigation

interface Navigator {
    suspend fun navigate(
        route: Route,
        saveState: Boolean = false,
        launchSingleTop: Boolean = false,
    )

    suspend fun navigateBack()
}
