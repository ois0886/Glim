package com.ssafy.glim.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import androidx.core.graphics.scale
import androidx.core.net.toUri
import com.ssafy.glim.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object DefaultImageUtils {

    suspend fun uriToBitmap(context: Context, uriString: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val uri = uriString.toUri()

                // 1. 원본 로드
                val original = context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                } ?: return@withContext null

                // 2. 회전 처리
                val rotated = context.contentResolver.openInputStream(uri)?.use { input ->
                    val exif = ExifInterface(input)
                    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    val rotation = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }

                    if (rotation != 0f) {
                        val matrix = Matrix().apply { postRotate(rotation) }
                        Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
                    } else {
                        original
                    }
                } ?: original

                // 3. 압축 (크기 + 품질)
                val resized = if (rotated.width > 800 || rotated.height > 800) {
                    val ratio = minOf(800f / rotated.width, 800f / rotated.height)
                    rotated.scale((rotated.width * ratio).toInt(), (rotated.height * ratio).toInt())
                } else {
                    rotated
                }

                // 4. 품질 압축
                var quality = 80
                while (quality > 20) {
                    val bytes = ByteArrayOutputStream()
                    resized.compress(Bitmap.CompressFormat.JPEG, quality, bytes)
                    if (bytes.size() <= 500 * 1024) break
                    quality -= 10
                }

                resized
            } catch (e: Exception) {
                Log.e("ImageUtils", "변환 실패", e)
                null
            }
        }
    }

    fun getDefaultProfileBitmap(context: Context): Bitmap? {
        return BitmapFactory.decodeResource(context.resources, R.drawable.base_profile)
    }
}
