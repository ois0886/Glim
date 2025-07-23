package com.ssafy.glim.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object SignUp : Route

    @Serializable
    data object UpdateInfo : Route

    @Serializable
    data class BookDetail(val bookId: Long) : BottomTabRoute
}

sealed interface GlimRoute : Route {
    @Serializable
    data object Liked : GlimRoute

    @Serializable
    data object Upload : GlimRoute
}

sealed interface BottomTabRoute : Route {
    @Serializable
    data object Home : BottomTabRoute

    @Serializable
    data object Library : BottomTabRoute

    @Serializable
    data object Post : BottomTabRoute

    @Serializable
    data object Reels : BottomTabRoute

    @Serializable
    data object Profile : BottomTabRoute
}
