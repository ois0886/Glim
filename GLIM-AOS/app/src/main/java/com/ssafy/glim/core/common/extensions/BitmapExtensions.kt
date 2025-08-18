package com.ssafy.glim.core.common.extensions

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.ssafy.glim.core.util.toBitmap
import java.io.File
import java.io.FileOutputStream

const val CACHE_DIR_NAME = "masked_images"
const val FILE_PREFIX = "masked_image_"
const val FILE_EXTENSION = ".jpg"
const val IMAGE_QUALITY = 90

// 확장 함수
fun Bitmap.toUri(context: Context): Uri? {
    return try {
        val cacheDir = createCacheDirectory(context)
        val outputFile = createOutputFile(cacheDir)
        saveToFile(outputFile)
        createFileProviderUri(context, outputFile)
    } catch (e: Exception) {
        Log.e("ImageOverlay", "URI 변환 실패", e)
        null
    }
}

suspend fun String.toUri(context: Context): Uri? {
    return this.toBitmap(context)?.toUri(context)

}

private fun createCacheDirectory(context: Context): File {
    val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
    if (!cacheDir.exists()) {
        cacheDir.mkdirs()
    }
    return cacheDir
}

private fun createOutputFile(cacheDir: File): File {
    val fileName = "${FILE_PREFIX}${System.currentTimeMillis()}${FILE_EXTENSION}"
    return File(cacheDir, fileName)
}

private fun Bitmap.saveToFile(outputFile: File) {
    FileOutputStream(outputFile).use { out ->
        compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, out)
        out.flush()
    }
}

private fun createFileProviderUri(context: Context, outputFile: File): Uri {
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        outputFile
    )
}
