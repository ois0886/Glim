package com.ssafy.glim.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.net.toUri
import com.ssafy.glim.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DefaultImageUtils {
    fun getDefaultProfileBitmap(context: Context): Bitmap? {
        return try {
            BitmapFactory.decodeResource(context.resources, R.drawable.base_profile)
        } catch (e: Exception) {
            Log.e("DefaultImageUtils", "기본 프로필 이미지 로드 실패", e)
            null
        }
    }

    suspend fun uriToBitmap(context: Context, uriString: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val uri = uriString.toUri()
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                Log.e("ImageUtils", "Bitmap 변환 실패", e)
                null
            }
        }
    }

}
