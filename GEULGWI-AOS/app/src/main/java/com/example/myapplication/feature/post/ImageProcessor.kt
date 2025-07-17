package com.example.myapplication.feature.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class ImageProcessor
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun uriToBitmap(uri: Uri): Bitmap? {
            return try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                null
            }
        }

        suspend fun recognizeText(uri: Uri): String {
            return suspendCancellableCoroutine { continuation ->
                try {
                    val inputImage = InputImage.fromFilePath(context, uri)
                    val recognizer =
                        TextRecognition.getClient(
                            KoreanTextRecognizerOptions.Builder().build(),
                        )

                    recognizer.process(inputImage)
                        .addOnSuccessListener { visionText ->
                            val result = visionText.text
                            continuation.resume(
                                if (result.isEmpty()) "텍스트를 찾을 수 없습니다" else result,
                            )
                        }
                        .addOnFailureListener { e ->
                            continuation.resume("텍스트 인식 실패: ${e.message}")
                        }
                } catch (e: Exception) {
                    continuation.resume("이미지 로드 실패: ${e.message}")
                }
            }
        }
    }
