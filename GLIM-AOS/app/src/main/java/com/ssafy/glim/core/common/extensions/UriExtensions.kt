package com.ssafy.glim.core.common.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log

fun Uri.toBitmap(context: Context): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, this)
            ImageDecoder.decodeBitmap(source)
        } else {
            context.contentResolver.openInputStream(this)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }
    } catch (e: Exception) {
        Log.e("BitmapConversion", "Failed to convert Uri to Bitmap", e)
        null
    }
}
