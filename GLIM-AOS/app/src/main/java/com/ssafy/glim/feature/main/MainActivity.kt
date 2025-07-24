package com.ssafy.glim.feature.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.ssafy.glim.core.navigation.LaunchedNavigator
import com.ssafy.glim.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri
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
    lateinit var lockServiceManager: LockServiceManager

    @RequiresApi(Build.VERSION_CODES.O)
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
            MyApplicationTheme {
                MainScreen(
                    navigator = navigator,
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startLockService() {
        lockServiceManager.start()
    }
}
