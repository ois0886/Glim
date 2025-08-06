package com.ssafy.glim.feature.lock

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.glim.core.util.saveImageToGallery
import com.ssafy.glim.feature.main.MainActivity
import com.ssafy.glim.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.compose.collectSideEffect

@AndroidEntryPoint
@SuppressLint("SourceLockedOrientationActivity")
class LockScreenActivity : ComponentActivity() {
    private var launchMainAfterUnlock = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launchMainAfterUnlock = intent
            .getBooleanExtra("was_in_app_before_lock", false)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            (getSystemService(KeyguardManager::class.java))
                ?.requestDismissKeyguard(this, null)
        }
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            // You can hide the caption bar even when the other system bars are visible.
            // To account for this, explicitly check the visibility of navigationBars()
            // and statusBars() rather than checking the visibility of systemBars().
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: LockViewModel = hiltViewModel()
                val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

                viewModel.collectSideEffect { effect ->
                    when (effect) {
                        is LockSideEffect.Unlock -> {
                            if (launchMainAfterUnlock) {
                                startActivity(
                                    Intent(this, MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                )
                                finish()
                            } else {
                                finishAffinity()
                            }
                        }

                        is LockSideEffect.ShowToast -> Toast.makeText(
                            this,
                            this.getString(effect.messageRes),
                            Toast.LENGTH_SHORT
                        ).show()

                        is LockSideEffect.NavigateQuotes -> {
                            this.startActivity(
                                Intent(this, MainActivity::class.java).apply {
                                    putExtra("nav_route", "glim")
                                    putExtra("quote_id", effect.quoteId)
                                }
                            )
                            (this as? Activity)?.finish()
                        }

                        is LockSideEffect.SaveImage -> saveImageToGallery(this, effect.imageUrl)

                        LockSideEffect.NavigateCamera -> {
                            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                LockScreenContent(
                    state = state,
                    tick = viewModel::tick,
                    nextQuote = viewModel::nextQuote,
                    prevQuote = viewModel::prevQuote,
                    unlockMain = viewModel::unlockMain,
                    saveGlim = viewModel::saveGlim,
                    toggleLike = viewModel::toggleLike,
                    openCamera = viewModel::openCamera,
                    viewQuote = viewModel::viewQuote,
                )
            }
        }
    }
}
