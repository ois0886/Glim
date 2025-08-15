package com.ssafy.glim.feature.main

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.ssafy.glim.R
import com.ssafy.glim.core.navigation.BottomTabRoute

internal enum class MainTab(
    val iconResId: Int,
    internal val contentDescription: String,
    val label: String,
    val route: BottomTabRoute
) {
    HOME(
        iconResId = R.drawable.ic_home,
        contentDescription = "Home Icon",
        label = "홈",
        route = BottomTabRoute.Home,
    ),
    LIBRARY(
        iconResId = R.drawable.ic_search_24,
        contentDescription = "Search Icon",
        label = "검색",
        route = BottomTabRoute.Search,
    ),
    POST(
        iconResId = R.drawable.icon_post,
        contentDescription = "Post Icon",
        label = "포스트",
        route = BottomTabRoute.Post(),
    ),
    REELS(
        iconResId = R.drawable.ic_glim,
        contentDescription = "Shorts Icon",
        label = "글:림",
        route = BottomTabRoute.Shorts(),
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
        fun contains(predicate: @Composable (NavKey) -> Boolean): Boolean {
            return entries.map { it.route }.any { predicate(it) }
        }
    }
}
