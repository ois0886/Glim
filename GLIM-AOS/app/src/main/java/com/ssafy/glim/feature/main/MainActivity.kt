package com.ssafy.glim.feature.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation3.runtime.rememberNavBackStack
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.glim.R
import com.ssafy.glim.core.data.authmanager.AuthManager
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

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var lockServiceManager: LockServiceManager

    @Inject
    lateinit var authManager: AuthManager

    private var isLoading by mutableStateOf(true)
    private var showNotificationPermissionDialog by mutableStateOf(false)

    // 알림 권한 요청 런처
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Permission", "알림 권한 허용됨")
            Toast.makeText(this, "알림 권한이 허용되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Permission", "알림 권한 거부됨")
            showNotificationPermissionDialog = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        if (PermissionUtil.alertPermissionCheck(this)) {
            PermissionUtil.onObtainingPermissionOverlayWindow(this)
        }
        enableEdgeToEdge()

        // 알림 권한 확인
        checkNotificationPermission()
        performInitialization()
        splashScreen.setKeepOnScreenCondition { isLoading }
        getFCMToken()

        setContent {
            MyApplicationTheme {

                var startDestination: Route? by remember { mutableStateOf(null) }

                LaunchedEffect(Unit) {
                    startDestination = if (authManager.canAutoLogin()) {
                        BottomTabRoute.Home
                    } else {
                        Route.Login
                    }
                }

                startDestination?.let { destination ->
                    val navBackStack = rememberNavBackStack(destination)

                    LaunchedNavigator(navBackStack)
                    val initialRoute = intent.getStringExtra("nav_route")

                    if (!isLoading) {
                        MainScreen(
                            backStack = navBackStack,
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

                        LaunchedEffect(initialRoute) {
                            if (destination == BottomTabRoute.Home && initialRoute == "glim") {
                                val quoteId = intent.getLongExtra("quote_id", -1L)
                                navBackStack.clear()
                                navBackStack.add(BottomTabRoute.Shorts(quoteId))
                            }
                        }
                    }
                }

                AppEventsHandler(
                    authManager = authManager,
                    backStack = navBackStack
                )

                if (showNotificationPermissionDialog) {
                    AlertDialog(
                        onDismissRequest = { showNotificationPermissionDialog = false },
                        title = { Text(stringResource(R.string.notification_permission_title)) },
                        text = {
                            Text(stringResource(R.string.notification_permission_message))
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    openAppSettings()
                                    showNotificationPermissionDialog = false
                                }
                            ) {
                                Text(stringResource(R.string.go_to_settings))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showNotificationPermissionDialog = false }
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("Permission", "알림 권한이 이미 허용됨")
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showPermissionRationale()
                }

                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Log.d("Permission", "Android 12 이하 - 알림 권한 자동 허용")
        }
    }

    private fun showPermissionRationale() {
        showNotificationPermissionDialog = true
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = ("package:$packageName").toUri()
        }
        startActivity(intent)
    }

    private fun performInitialization() {
        isLoading = false
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "토큰 가져오기 실패", task.exception)
                return@addOnCompleteListener
            }

            // 토큰 획득
            val token = task.result
            Log.d("FCM", "FCM 토큰: $token")
        }
    }
}
