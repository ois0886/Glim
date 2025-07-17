package com.example.myapplication.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.myapplication.core.navigation.Route
import com.example.myapplication.feature.home.navigation.navigateToHome
import com.example.myapplication.feature.library.navigation.navigateToLibrary
import com.example.myapplication.feature.post.navigation.navigateToPost
import com.example.myapplication.feature.profile.navigation.navigateToProfile
import com.example.myapplication.feature.reels.navigation.navigateToReels

internal class MainNavController(
    val navController: NavHostController,
) {
    private val currentDestination: NavDestination?
        @Composable get() =
            navController
                .currentBackStackEntryAsState().value?.destination

    val startDestination = Route.Login

    val currentTab: MainTab?
        @Composable get() =
            MainTab.find { tab ->
                currentDestination?.hasRoute(tab::class) == true
            }

    @Composable
    fun shouldShowBottomBar() =
        MainTab.contains {
            currentDestination?.hasRoute(it::class) == true
        }

    fun navigate(tab: MainTab) {
        val navOptions =
            navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }

        when (tab) {
            MainTab.HOME -> navController.navigateToHome(navOptions)
            MainTab.LIBRARY -> navController.navigateToLibrary(navOptions)
            MainTab.POST -> navController.navigateToPost(navOptions)
            MainTab.REELS -> navController.navigateToReels(navOptions)
            MainTab.PROFILE -> navController.navigateToProfile(navOptions)
        }
    }

    fun popBackStack() {
        navController.popBackStack()
    }

    fun clearBackStack() {
        val options =
            NavOptions.Builder()
                .setPopUpTo(navController.graph.findStartDestination().id, inclusive = false)
                .build()
        navController.navigate(startDestination, options)
    }
}

@Composable
internal fun rememberMainNavController(navController: NavHostController = rememberNavController()): MainNavController =
    remember(navController) {
        MainNavController(navController)
    }
