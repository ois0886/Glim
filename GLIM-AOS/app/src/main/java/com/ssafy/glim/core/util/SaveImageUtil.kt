package com.ssafy.glim.core.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import coil.Coil.imageLoader
import coil.request.ImageRequest
import com.ssafy.glim.R

suspend fun saveImageToGallery(context: Context, imageUrl: String) {
    val bitmap = imageUrl.toBitmap(context) ?: return

    // MediaStore 에 저장
    val filename = "glim_${System.currentTimeMillis()}.jpg"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri == null) {
        Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
        return
    }
    resolver.openOutputStream(uri)?.use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    }

    Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_SHORT).show()
}

suspend fun String.toBitmap(context: Context): Bitmap? {
    val request = ImageRequest.Builder(context)
        .data(this)
        .allowHardware(false)
        .build()
    val result = imageLoader(context).execute(request)
    return (result.drawable as? BitmapDrawable)?.bitmap
}
