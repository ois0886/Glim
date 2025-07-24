package com.ssafy.glim.core.data.extensions

import android.graphics.Bitmap
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

fun Bitmap.toImagePart(
    partName: String = "quoteImage",
    fileName: String = "quote.jpg",
    quality: Int = 85
): MultipartBody.Part? {
    return try {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val imageBytes = outputStream.toByteArray()

        val requestBody =
            imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0)
        MultipartBody.Part.createFormData(partName, fileName, requestBody)
    } catch (e: Exception) {
        Log.e("BitmapToImagePart", "Failed to convert bitmap", e)
        null
    }
}
