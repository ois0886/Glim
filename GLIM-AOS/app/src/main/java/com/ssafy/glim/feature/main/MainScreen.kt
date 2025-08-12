package com.ssafy.glim.feature.main

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.MyGlimsRoute
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.core.navigation.UpdateInfoRoute
import com.ssafy.glim.feature.auth.login.LoginRoute
import com.ssafy.glim.feature.bookdetail.BookDetailScreen
import com.ssafy.glim.feature.celebrations.CelebrationsRoute
import com.ssafy.glim.feature.home.HomeRoute
import com.ssafy.glim.feature.main.component.MainBottomBar
import com.ssafy.glim.feature.myglims.MyGlimsRoute
import com.ssafy.glim.feature.post.PostRoute
import com.ssafy.glim.feature.profile.ProfileRoute
import com.ssafy.glim.feature.search.SearchRoute
import com.ssafy.glim.feature.setting.SettingRoute
import com.ssafy.glim.feature.shorts.ShortsRoute
import com.ssafy.glim.feature.update.UpdateRoute
import com.ssafy.glim.feature.update.UpdateType
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun MainScreen(
    backStack: NavBackStack,
    onTabSelected: (MainTab) -> Unit,
) {
    // 현재 라우트와 탭 계산
    val currentRoute = backStack.lastOrNull()
    val currentTab = when(currentRoute) {
        is BottomTabRoute.Home -> MainTab.HOME
        is BottomTabRoute.Post -> MainTab.POST
        is BottomTabRoute.Search -> MainTab.LIBRARY
        is BottomTabRoute.Shorts -> MainTab.REELS
        is BottomTabRoute.Profile -> MainTab.PROFILE
        else -> MainTab.HOME
    }

    Scaffold(
        bottomBar = {
            if (currentRoute !is BottomTabRoute.Post && currentRoute !is Route.BookDetail) {
                MainBottomBar(
                    tabs = MainTab.entries.toImmutableList(),
                    currentTab = currentTab,
                    onTabSelected = onTabSelected,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        NavDisplay(
            entryDecorators = listOf(
                // Add the default decorators for managing scenes and saving state
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                // Then add the view model store decorator
//                            rememberViewModelStoreNavEntryDecorator()
            ),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = {
                ContentTransform(
                    fadeIn(animationSpec = tween(0)),
                    fadeOut(animationSpec = tween(0)),
                )
            },
            popTransitionSpec = {
                ContentTransform(
                    fadeIn(animationSpec = tween(0)),
                    fadeOut(animationSpec = tween(0)),
                )
            },
            modifier = Modifier.background(Color.White),
            entryProvider = { key ->
                when(key) {
                    is BottomTabRoute.Home -> NavEntry(key) {
                        HomeRoute(
                            padding = innerPadding,
                        )
                    }

                    is BottomTabRoute.Search -> NavEntry(key) {
                        SearchRoute(
                            padding = innerPadding,
                            popBackStack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }

                    is BottomTabRoute.Post -> NavEntry(key) {
                        PostRoute(
                            bookId = key.bookId,
                            padding = innerPadding,
                            popBackStack = {
                                backStack.removeLastOrNull()
                            },
                        )
                    }

                    is BottomTabRoute.Profile -> NavEntry(key) {
                        ProfileRoute(
                            padding = innerPadding,
                        )
                    }

                    is MyGlimsRoute -> NavEntry(key) {
                        MyGlimsRoute(
                            padding = innerPadding,
                        )
                    }

                    is Route.Celebration -> NavEntry(key) {
                        CelebrationsRoute(
                            padding = innerPadding,
                            nickname = key.nickname,
                        )
                    }

                    is Route.Login -> NavEntry(key) {
                        LoginRoute(
                            padding = innerPadding,
                        )
                    }

                    is Route.BookDetail -> NavEntry(key) {
                        BookDetailScreen(
                            isbn = key.isbn,
                            bookId = key.bookId,
                            padding = innerPadding,
                            popBackStack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }

                    is BottomTabRoute.Shorts -> NavEntry(key) {
                        ShortsRoute(
                            quoteId = key.quoteId,
                            padding = innerPadding,
                        )
                    }

                    is Route.Setting -> NavEntry(key) {
                        SettingRoute(
                            padding = innerPadding,
                        )
                    }

                    is UpdateInfoRoute.Password -> NavEntry(key) {
                        UpdateRoute(
                            updateType = UpdateType.PASSWORD,
                            padding = innerPadding,
                        )
                    }

                    is UpdateInfoRoute.Personal -> NavEntry(key) {
                        UpdateRoute(
                            updateType = UpdateType.PERSONAL,
                            padding = innerPadding,
                        )
                    }

                    else -> NavEntry(key) { Unit }
                }
            },
        )
    }
}

@Composable
fun PaddingValues.excludeSystemBars(): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    return PaddingValues(
        start = calculateStartPadding(layoutDirection),
        top = calculateTopPadding() - systemBarsPadding.calculateTopPadding(),
        end = calculateEndPadding(layoutDirection),
        bottom = calculateBottomPadding(),
    )
}
