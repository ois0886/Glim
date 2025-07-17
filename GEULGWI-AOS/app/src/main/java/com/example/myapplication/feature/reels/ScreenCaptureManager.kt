// CaptureUtils.kt
package com.example.myapplication.feature.reels

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * 화면 캡처를 위한 유틸리티 클래스
 */
class ScreenCaptureManager
    @Inject
    constructor(
        @ApplicationContext val context: Context,
    ) {
        /**
         * GraphicsLayer를 캡처해서 갤러리에 저장
         */
        suspend fun captureAndSaveToGallery(
            graphicsLayer: GraphicsLayer,
            fileName: String? = null,
        ): Boolean {
            return try {
                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                saveBitmapToGallery(bitmap, fileName)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        /**
         * Bitmap을 갤러리에 저장
         */
        private suspend fun saveBitmapToGallery(
            bitmap: Bitmap,
            customFileName: String? = null,
        ): Boolean {
            return withContext(Dispatchers.IO) {
                try {
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val fileName = customFileName ?: "Glim_$timestamp.jpg"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        saveToMediaStore(bitmap, fileName)
                    } else {
                        saveToExternalStorage(bitmap, fileName)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        /**
         * Android 10 이상 - MediaStore 사용
         */
        private fun saveToMediaStore(
            bitmap: Bitmap,
            fileName: String,
        ): Boolean {
            val values =
                ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Glim")
                }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            return uri?.let { imageUri ->
                context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                true
            } ?: false
        }

        /**
         * Android 9 이하 - 외부 저장소 직접 접근
         */
        private fun saveToExternalStorage(
            bitmap: Bitmap,
            fileName: String,
        ): Boolean {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val glimDir = File(picturesDir, "Glim")
            if (!glimDir.exists()) {
                glimDir.mkdirs()
            }

            val file = File(glimDir, fileName)
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            // 미디어 스캔 알림
            val values =
                ContentValues().apply {
                    put(MediaStore.Images.Media.DATA, file.absolutePath)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            return true
        }

        /**
         * Toast 메시지 표시
         */
        fun showSaveResult(success: Boolean) {
            val message =
                if (success) {
                    "이미지가 저장되었습니다!"
                } else {
                    "이미지 저장에 실패했습니다."
                }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

/**
 * Composable에서 사용할 수 있는 캡처 기능 훅
 */
@Composable
fun rememberScreenCapture(context: Context = androidx.compose.ui.platform.LocalContext.current): ScreenCaptureManager {
    return remember { ScreenCaptureManager(context) }
}

/**
 * 캡처 액션을 생성하는 헬퍼 함수
 */
@Composable
fun rememberCaptureAction(
    graphicsLayer: GraphicsLayer,
    fileName: String? = null,
): () -> Unit {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val captureManager = rememberScreenCapture(context)

    return {
        coroutineScope.launch {
            try {
                val success = captureManager.captureAndSaveToGallery(graphicsLayer, fileName)
                captureManager.showSaveResult(success)
            } catch (e: Exception) {
                Toast.makeText(context, "캡처 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}
