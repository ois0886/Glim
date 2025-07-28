package com.ssafy.glim.core.navigation.internal.viewmodel

import com.ssafy.glim.core.navigation.Route

sealed interface RouteSideEffect {
    data class Navigate(
        val route: Route,
        val saveState: Boolean,
        val launchSingleTop: Boolean,
        val inclusive: Boolean
    ) : RouteSideEffect

    data object NavigateBack : RouteSideEffect
}
