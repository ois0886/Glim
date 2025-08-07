package com.ssafy.glim.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.ssafy.glim.feature.lock.LockScreenActivity
import jakarta.inject.Inject
import androidx.core.content.edit

class ScreenReceiver @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val prefs = context.getSharedPreferences("lock_prefs", Context.MODE_PRIVATE)
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> {
                val wasInApp = ProcessLifecycleOwner
                    .get().lifecycle.currentState
                    .isAtLeast(Lifecycle.State.STARTED)
                prefs.edit { putBoolean("was_in_app_before_lock", wasInApp) }
            }

            Intent.ACTION_SCREEN_ON -> {
                val wasInApp = prefs.getBoolean("was_in_app_before_lock", false)
                context.startActivity(
                    Intent(context, LockScreenActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        putExtra("was_in_app_before_lock", wasInApp)
                    }
                )
            }
        }
    }
}
