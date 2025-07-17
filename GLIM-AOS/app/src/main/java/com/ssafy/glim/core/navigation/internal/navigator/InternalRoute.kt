package com.ssafy.glim.core.navigation.internal.navigator

import com.ssafy.glim.core.navigation.Route

sealed interface InternalRoute {
    data class Navigate(
        val route: Route,
        val saveState: Boolean,
        val launchSingleTop: Boolean,
    ) : InternalRoute

    data object NavigateBack : InternalRoute
}
