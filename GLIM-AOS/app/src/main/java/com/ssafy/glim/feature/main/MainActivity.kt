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
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.LaunchedNavigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.core.service.LockServiceManager
import com.ssafy.glim.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
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

    private var isLoading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        android.util.Log.d("Splash", "스플래쉬 시작")
        val splashScreen = installSplashScreen()
        android.util.Log.d("Splash", "스플래쉬 설치됨")
        super.onCreate(savedInstanceState)

        if (PermissionUtil.alertPermissionCheck(this)) {
            PermissionUtil.onObtainingPermissionOverlayWindow(this)
        }
        enableEdgeToEdge()

        performInitialization()
        splashScreen.setKeepOnScreenCondition { isLoading }

        setContent {
            val navigator: MainNavController = rememberMainNavController()
            LaunchedNavigator(navigator.navController)
            val initialRoute = intent.getStringExtra("nav_route")
            MyApplicationTheme {
                if (!isLoading) {
                    MainScreen(
                        navigator = navigator,
                    )
                    LaunchedEffect(initialRoute) {
                        if (initialRoute == "book") {
                            val isbn = intent.getStringExtra("isbn") ?: ""
                            navigator.navController.navigate(Route.BookDetail(isbn)) {
                                popUpTo(navigator.startDestination) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        } else if (initialRoute == "glim") {
                            val quoteId = intent.getLongExtra("quote_id", -1L)
                            navigator.clearBackStackAndNavigate(BottomTabRoute.Shorts(quoteId))
                        }
                    }
                }
            }

        }
    }

    private fun performInitialization() {
        Thread {
            try {
                startLockService()
                Thread.sleep(1500)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                runOnUiThread {
                    isLoading = false
                }
            }
        }.start()
    }

    private fun startLockService() {
        lockServiceManager.start()
    }
}
