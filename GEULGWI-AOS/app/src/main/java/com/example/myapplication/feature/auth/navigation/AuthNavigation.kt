package com.example.myapplication.feature.auth.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.core.navigation.Route
import com.example.myapplication.feature.auth.login.LoginRoute
import com.example.myapplication.feature.auth.signup.SignUpRoute

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
