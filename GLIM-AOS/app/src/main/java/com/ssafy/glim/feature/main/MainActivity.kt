package com.ssafy.glim.feature.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.LaunchedNavigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.core.service.LockServiceManager
import com.ssafy.glim.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue

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

    private val viewModel: MainViewModel by viewModels()

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

        // 딥링크 처리 추가
        handleDeepLink(intent)

        performInitialization()
        splashScreen.setKeepOnScreenCondition { isLoading }

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                var startDestination: Route? by remember { mutableStateOf(null) }

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
                    val deepLinkQuoteId = intent.getLongExtra("quote_id", -1L)

                    if (!isLoading) {
                        MainScreen(
                            navigator = navigator,
                            onTabSelected = {
                                when (it.route) {
                                    BottomTabRoute.Home -> viewModel.navigateHome()
                                    is BottomTabRoute.Post -> viewModel.navigatePost()
                                    BottomTabRoute.Profile -> viewModel.navigateProfile()
                                    BottomTabRoute.Search -> viewModel.navigateSearch()
                                    is BottomTabRoute.Shorts -> viewModel.navigateQuote(-1L)
                                }
                            },
                        )

                        // 딥링크 네비게이션 처리 수정
                        LaunchedEffect(initialRoute, deepLinkQuoteId) {
                            when {
                                // 딥링크로 특정 명언 열기
                                deepLinkQuoteId > 0 -> {
                                    navigator.clearBackStackAndNavigate(BottomTabRoute.Shorts(deepLinkQuoteId))
                                }
                                // 기존 로직
                                destination == BottomTabRoute.Home && initialRoute == "glim" -> {
                                    val quoteId = intent.getLongExtra("quote_id", -1L)
                                    navigator.clearBackStackAndNavigate(BottomTabRoute.Shorts(quoteId))
                                }
                            }
                        }
                    }
                }

                AppEventsHandler(
                    authManager = authManager,
                    navController = navController
                )
            }
        }
    }

    // 새 Intent 처리 (앱이 이미 실행 중일 때)
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return

        if (data.scheme == "glim") {
            when (data.host) {
                "quote" -> {
                    val pathSegments = data.pathSegments
                    if (!pathSegments.isNullOrEmpty()) {
                        val quoteId = pathSegments[0].toLongOrNull()

                        if (quoteId != null && quoteId > 0) {
                            intent.putExtra("nav_route", "deeplink_quote")
                            intent.putExtra("quote_id", quoteId)
                        }
                    }
                }

                "home" -> {
                    intent.putExtra("nav_route", "deeplink_home")
                }
            }
        }
    }

    private fun performInitialization() {
        isLoading = false
    }
}
