package com.example.myapplication.core.navigation.internal.navigator

import com.example.myapplication.core.navigation.Route

sealed interface InternalRoute {
    data class Navigate(
        val route: Route,
        val saveState: Boolean,
        val launchSingleTop: Boolean,
    ) : InternalRoute

    data object NavigateBack : InternalRoute
}
