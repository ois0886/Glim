package com.ssafy.glim.feature.main

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation3.runtime.NavBackStack
import com.ssafy.glim.R
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.navigation.Route
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppEventsHandler(
    authManager: AuthManager,
    backStack: NavBackStack
) {
    val context = LocalContext.current

    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        authManager.logoutEvent.collectLatest { reason ->
            dialogMessage = context.getString(reason.messageRes)
            showLogoutDialog = true
        }
    }

    if (showLogoutDialog) {
        LaunchedEffect(Unit) {
            backStack.clear()
            backStack.add(Route.Login)
        }

        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(text = stringResource(R.string.logout_title))
            },
            text = {
                Text(text = dialogMessage)
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                }) {
                    Text(text = stringResource(R.string.confirm))
                }
            }
        )
    }
}
