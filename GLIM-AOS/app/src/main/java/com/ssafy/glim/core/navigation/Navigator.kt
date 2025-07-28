package com.ssafy.glim.core.navigation

interface Navigator {
    suspend fun navigate(
        route: Route,
        saveState: Boolean = false,
        launchSingleTop: Boolean = false,
        inclusive: Boolean = false
    )

    suspend fun navigateBack()
}
