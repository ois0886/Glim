package com.example.myapplication.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.myapplication.core.navigation.internal.viewmodel.NavigatorViewModel
import com.example.myapplication.core.navigation.internal.viewmodel.RouteSideEffect
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LaunchedNavigator(navHostController: NavHostController) {
    InternalLaunchedNavigator(
        navHostController = navHostController,
    )
}

@Composable
private fun InternalLaunchedNavigator(
    navHostController: NavHostController,
    routerViewModel: NavigatorViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(routerViewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            routerViewModel.sideEffect.collectLatest { sideEffect ->
                when (sideEffect) {
                    is RouteSideEffect.NavigateBack -> {
                        navHostController.popBackStack()
                    }
                    is RouteSideEffect.Navigate -> {
                        navHostController.navigate(sideEffect.route) {
                            if (sideEffect.saveState) {
                                navHostController.graph.findStartDestination().route?.let {
                                    popUpTo(it) {
                                        saveState = true
                                    }
                                }
                                restoreState = true
                            }
                            launchSingleTop = sideEffect.launchSingleTop
                        }
                    }
                }
            }
        }
    }
}
