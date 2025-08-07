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
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.LaunchedNavigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.core.service.LockServiceManager
import com.ssafy.glim.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var authManager: AuthManager

    private var isLoading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        if (PermissionUtil.alertPermissionCheck(this)) {
            PermissionUtil.onObtainingPermissionOverlayWindow(this)
        }
        enableEdgeToEdge()

        performInitialization()
        splashScreen.setKeepOnScreenCondition { isLoading }

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                var startDestination: Route? by remember { mutableStateOf(null) }

                // 초기 목적지 결정
                LaunchedEffect(Unit) {
                    startDestination = if (authManager.canAutoLogin()) {
                        BottomTabRoute.Home
                    } else {
                        Route.Login
                    }
                }

                startDestination?.let { destination ->
                    val navigator: MainNavController = rememberMainNavController(
                        navController = navController,
                        startDestination = destination
                    )
                    LaunchedNavigator(navigator.navController)
                    val initialRoute = intent.getStringExtra("nav_route")

                    if (!isLoading) {
                        MainScreen(navigator = navigator)

                        // 딥링크 처리
                        LaunchedEffect(initialRoute) {
                            if (destination == BottomTabRoute.Home && initialRoute == "glim") {
                                val quoteId = intent.getLongExtra("quote_id", -1L)
                                navigator.clearBackStackAndNavigate(BottomTabRoute.Shorts(quoteId))
                            }
                        }
                    }
                }
                // 글로벌 이벤트 핸들러
                AppEventsHandler(
                    authManager = authManager,
                    navController = navController
                )
            }
        }
    }

    private fun performInitialization() {
        // lifecycleScope를 사용해서 코루틴에서 호출
        lifecycleScope.launch {
            try {
                startLockService()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                runOnUiThread {
                    isLoading = false
                }
            }
        }
    }

    private suspend fun startLockService() {
        // suspend 함수를 suspend 함수에서 호출
        lockServiceManager.start()
    }
}
