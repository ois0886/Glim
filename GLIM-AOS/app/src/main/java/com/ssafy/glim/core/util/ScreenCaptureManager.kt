// Enhanced CaptureUtils.kt
package com.ssafy.glim.core.util

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
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * 화면 캡처를 위한 유틸리티 클래스
 */
class ScreenCaptureManager @Inject constructor(
    @ApplicationContext val context: Context,
) {

    /**
     * GraphicsLayer를 Bitmap으로 변환 (저장하지 않고 반환만)
     */
    suspend fun captureToBitmap(graphicsLayer: GraphicsLayer): Bitmap? {
        return try {
            graphicsLayer.toImageBitmap().asAndroidBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * GraphicsLayer를 ByteArray로 변환
     */
    suspend fun captureToByteArray(
        graphicsLayer: GraphicsLayer,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 100
    ): ByteArray? {
        return try {
            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
            bitmapToByteArray(bitmap, format, quality)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Bitmap을 ByteArray로 변환
     */
    fun bitmapToByteArray(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 100
    ): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)
        return stream.toByteArray()
    }

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
     * GraphicsLayer를 캡처해서 임시 파일로 저장
     */
    suspend fun captureToTempFile(
        graphicsLayer: GraphicsLayer,
        fileName: String? = null,
    ): File? {
        return try {
            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
            saveBitmapToTempFile(bitmap, fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * GraphicsLayer를 캡처해서 캐시 디렉토리에 저장
     */
    suspend fun captureToCacheFile(
        graphicsLayer: GraphicsLayer,
        fileName: String? = null,
    ): File? {
        return try {
            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
            saveBitmapToCacheFile(bitmap, fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Bitmap을 갤러리에 저장
     */
    suspend fun saveBitmapToGallery(
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
     * Bitmap을 임시 파일로 저장
     */
    suspend fun saveBitmapToTempFile(
        bitmap: Bitmap,
        customFileName: String? = null,
    ): File? {
        return withContext(Dispatchers.IO) {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = customFileName ?: "temp_$timestamp.jpg"

                val tempFile = File(context.cacheDir, fileName)
                FileOutputStream(tempFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                tempFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Bitmap을 캐시 디렉토리에 저장
     */
    suspend fun saveBitmapToCacheFile(
        bitmap: Bitmap,
        customFileName: String? = null,
    ): File? {
        return withContext(Dispatchers.IO) {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = customFileName ?: "cache_$timestamp.jpg"

                val cacheDir = File(context.cacheDir, "screenshots")
                if (!cacheDir.exists()) cacheDir.mkdirs()

                val cacheFile = File(cacheDir, fileName)
                FileOutputStream(cacheFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                cacheFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
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
fun rememberScreenCapture(context: Context = LocalContext.current): ScreenCaptureManager {
    return remember { ScreenCaptureManager(context) }
}

/**
 * 다양한 캡처 액션들을 제공하는 클래스
 */
data class CaptureActions(
    val saveToGallery: () -> Unit,
    val getBitmap: suspend () -> Bitmap?,
    val getByteArray: suspend () -> ByteArray?,
    val saveToTempFile: suspend () -> File?,
    val saveToCacheFile: suspend () -> File?,
)

/**
 * 캡처 액션들을 생성하는 헬퍼 함수
 */
@Composable
fun rememberCaptureActions(
    graphicsLayer: GraphicsLayer,
    fileName: String? = null,
): CaptureActions {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val captureManager = rememberScreenCapture(context)

    return remember(graphicsLayer, fileName) {
        CaptureActions(
            saveToGallery = {
                coroutineScope.launch {
                    try {
                        val success = captureManager.captureAndSaveToGallery(graphicsLayer, fileName)
                        captureManager.showSaveResult(success)
                    } catch (e: Exception) {
                        Toast.makeText(context, "캡처 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            },
            getBitmap = {
                captureManager.captureToBitmap(graphicsLayer)
            },
            getByteArray = {
                captureManager.captureToByteArray(graphicsLayer)
            },
            saveToTempFile = {
                captureManager.captureToTempFile(graphicsLayer, fileName)
            },
            saveToCacheFile = {
                captureManager.captureToCacheFile(graphicsLayer, fileName)
            }
        )
    }
}

/**
 * 기존 함수 유지 (호환성)
 */
@Composable
fun rememberCaptureAction(
    graphicsLayer: GraphicsLayer,
    fileName: String? = null,
): () -> Unit {
    val captureActions = rememberCaptureActions(graphicsLayer, fileName)
    return captureActions.saveToGallery
}
