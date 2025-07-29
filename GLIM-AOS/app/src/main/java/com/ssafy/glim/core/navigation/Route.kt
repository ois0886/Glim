package com.ssafy.glim.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object SignUp : Route

    @Serializable
    data class BookDetail(val bookId: Long) : BottomTabRoute
}

sealed interface MyGlimsRoute : Route {

    @Serializable
    data object Liked : MyGlimsRoute

    @Serializable
    data object Upload : MyGlimsRoute
}

sealed class UpdateInfoRoute : Route {
    @Serializable
    data object Personal : Route

    @Serializable
    data object Password : Route
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
