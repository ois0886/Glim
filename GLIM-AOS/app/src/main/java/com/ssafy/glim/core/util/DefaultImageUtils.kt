package com.ssafy.glim.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.ssafy.glim.R

object DefaultImageUtils {
    fun getDefaultProfileBitmap(context: Context): Bitmap? {
        return try {
            BitmapFactory.decodeResource(context.resources, R.drawable.base_profile)
        } catch (e: Exception) {
            Log.e("DefaultImageUtils", "기본 프로필 이미지 로드 실패", e)
            null
        }
    }
}
