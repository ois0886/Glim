package com.example.myapplication.core.navigation.internal.viewmodel

import com.example.myapplication.core.navigation.Route

sealed interface RouteSideEffect {
    data class Navigate(
        val route: Route,
        val saveState: Boolean,
        val launchSingleTop: Boolean,
    ) : RouteSideEffect

    data object NavigateBack : RouteSideEffect
}
