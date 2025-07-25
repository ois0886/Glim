package com.ssafy.glim.feature.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ssafy.glim.core.navigation.LaunchedNavigator
import com.ssafy.glim.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.core.service.LockServiceManager
import javax.inject.Inject

private const val REQ_CODE_OVERLAY_PERMISSION: Int = 0

object PermissionUtil {
    fun onObtainingPermissionOverlayWindow(context: Activity) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            ("package:" + context.packageName).toUri()
        )
        context.startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION)
    }

    fun alertPermissionCheck(context: Context?): Boolean {
        return !Settings.canDrawOverlays(context)
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var lockServiceManager: LockServiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PermissionUtil.alertPermissionCheck(this)) {
            PermissionUtil.onObtainingPermissionOverlayWindow(this)
        }
        enableEdgeToEdge()

        startLockService()

        setContent {
            val navigator: MainNavController = rememberMainNavController()
            LaunchedNavigator(navigator.navController)

            var startDestination: Route? by remember { mutableStateOf(null) }

            LaunchedEffect(Unit) {
                val accessToken = authManager.getAccessToken()
                navigator.startDestination = if (accessToken != null) BottomTabRoute.Home else Route.Login
                startDestination = navigator.startDestination
            }

            startDestination?.let {
                MyApplicationTheme {
                    MainScreen(
                        navigator = navigator,
                    )
                }
            }
        }

    }

    private fun startLockService() {
        lockServiceManager.start()
    }
}
