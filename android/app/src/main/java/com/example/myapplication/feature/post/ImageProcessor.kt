package com.example.myapplication.feature.post

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class ImageProcessor
    @Inject
    constructor() {
        suspend fun loadBitmap(
            context: Context,
            uri: Uri,
        ): Bitmap? {
            return try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                bitmap
            } catch (e: Exception) {
                null
            }
        }

        suspend fun recognizeText(
            context: Context,
            uri: Uri,
        ): String {
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
