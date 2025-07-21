package com.ssafy.glim.app

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import com.ssafy.glim.core.receiver.ScreenReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlimApplication : Application()
