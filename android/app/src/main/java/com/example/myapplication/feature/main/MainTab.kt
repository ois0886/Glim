package com.example.myapplication.feature.main

import androidx.compose.runtime.Composable
import com.example.myapplication.R
import com.example.myapplication.core.navigation.BottomTabRoute
import com.example.myapplication.core.navigation.Route

internal enum class MainTab(
    val iconResId: Int,
    internal val contentDescription: String,
    val label: String,
    val route: BottomTabRoute,
) {
    HOME(
        iconResId = R.drawable.ic_home,
        contentDescription = "Home Icon",
        label = "홈",
        route = BottomTabRoute.Home,
    ),
    LIBRARY(
        iconResId = R.drawable.ic_library,
        contentDescription = "Library Icon",
        label = "서재",
        route = BottomTabRoute.Library,
    ),
    POST(
        iconResId = R.drawable.icon_post,
        contentDescription = "Post Icon",
        label = "포스트",
        route = BottomTabRoute.Post,
    ),
    REELS(
        iconResId = R.drawable.ic_reels,
        contentDescription = "Reels Icon",
        label = "릴스",
        route = BottomTabRoute.Reels,
    ),
    PROFILE(
        iconResId = R.drawable.ic_profile,
        contentDescription = "Profile Icon",
        label = "나의 정보",
        route = BottomTabRoute.Profile,
    ),
    ;

    companion object {
        @Composable
        fun find(predicate: @Composable (BottomTabRoute) -> Boolean): MainTab? {
            return entries.find { predicate(it.route) }
        }

        @Composable
        fun contains(predicate: @Composable (Route) -> Boolean): Boolean {
            return entries.map { it.route }.any { predicate(it) }
        }
    }
}
