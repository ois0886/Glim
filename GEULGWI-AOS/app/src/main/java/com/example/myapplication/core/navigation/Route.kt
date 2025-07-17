package com.example.myapplication.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object SignUp : Route
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
