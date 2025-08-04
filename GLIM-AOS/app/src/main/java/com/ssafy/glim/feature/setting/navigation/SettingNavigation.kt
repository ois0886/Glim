package com.ssafy.glim.feature.setting.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.setting.SettingRoute

fun NavGraphBuilder.settingNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<Route.Setting> {
        SettingRoute(
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}
