package com.ssafy.glim.core.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

fun ByteArray.toCacheImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "ai_${System.currentTimeMillis()}.png")
    file.outputStream().use { it.write(this) }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

fun Bitmap.toCacheImageUri(context: Context): Uri =
    ByteArrayOutputStream().use { bos ->
        this.compress(Bitmap.CompressFormat.PNG, 100, bos)
        bos.toByteArray().toCacheImageUri(context)
    }
