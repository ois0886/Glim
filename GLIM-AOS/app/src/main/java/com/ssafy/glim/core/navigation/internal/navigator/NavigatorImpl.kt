package com.ssafy.glim.core.navigation.internal.navigator

import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel

@ActivityRetainedScoped
class NavigatorImpl
@Inject
constructor() : Navigator, InternalNavigator {
    override val channel = Channel<InternalRoute>(Channel.BUFFERED)

    override suspend fun navigate(
        route: Route,
        saveState: Boolean,
        launchSingleTop: Boolean,
        inclusive: Boolean
    ) {
        channel.send(
            InternalRoute.Navigate(
                route = route,
                saveState = saveState,
                launchSingleTop = launchSingleTop,
                inclusive = inclusive
            ),
        )
    }

    override suspend fun navigateBack() {
        channel.send(InternalRoute.NavigateBack)
    }
}
