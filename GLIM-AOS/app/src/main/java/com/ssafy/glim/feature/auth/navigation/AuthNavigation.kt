package com.example.glim.feature.auth.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.auth.login.LoginRoute
import com.ssafy.glim.feature.auth.signup.SignUpRoute

fun NavGraphBuilder.authNavGraph(padding: PaddingValues) {
    composable<Route.Login> {
        LoginRoute(
            padding = padding,
        )
    }
    composable<Route.SignUp> {
        SignUpRoute()
    }
}
