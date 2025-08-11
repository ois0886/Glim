import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.FileProvider
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.util.ScreenCaptureManager

class ShareWithImageManager(
    private val context: Context,
    private val screenCaptureManager: ScreenCaptureManager
) {

    suspend fun shareQuoteWithImage(
        graphicsLayer: GraphicsLayer,
        quote: Quote,
        includeFallbackUrl: Boolean = true
    ) {
        try {
            // 1. GraphicsLayer를 캐시 파일로 저장
            val imageFile = screenCaptureManager.captureToCacheFile(
                graphicsLayer = graphicsLayer,
                fileName = "quote_share_${quote.quoteId}_${System.currentTimeMillis()}.jpg"
            )

            if (imageFile != null) {
                // 2. FileProvider를 통해 URI 생성
                val imageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    imageFile
                )

                // 3. 공유 텍스트 구성
                val shareText = buildShareText(quote, includeFallbackUrl)

                // 4. 공유 Intent 생성 (기존 작동하던 방식 그대로)
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "image/*"  // 기존처럼 image/*로 되돌림
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // 먼저 텍스트만 공유 테스트
                val testIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(Intent.createChooser(testIntent, "텍스트 테스트"))

                // 5. 공유 실행 (기존처럼 간단하게)
                val chooser = Intent.createChooser(testIntent, "명언 공유하기")
                context.startActivity(chooser)

            } else {
                // 캡처 실패 시 텍스트만 공유
                shareTextOnly(quote, includeFallbackUrl)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            shareTextOnly(quote, includeFallbackUrl)
        }
    }

    suspend fun shareQuoteWithImageToGallery(
        graphicsLayer: GraphicsLayer,
        quote: Quote
    ): Boolean {
        return try {
            screenCaptureManager.captureAndSaveToGallery(
                graphicsLayer = graphicsLayer,
                fileName = "Glim_Quote_${quote.quoteId}_${System.currentTimeMillis()}.jpg"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun buildShareText(quote: Quote, includeFallbackUrl: Boolean): String {
        return """
        "${quote.content}" - ${quote.author}
        
        Glim 앱에서 더 많은 명언을 만나보세요!
        https://68999f56ccd032dec0465b2b--venerable-brigadeiros-b967ae.netlify.app/?quote=${quote.quoteId}
        
    """.trimIndent()
    }

    private fun shareTextOnly(quote: Quote, includeFallbackUrl: Boolean) {
        val shareText = buildShareText(quote, includeFallbackUrl)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Glim에서 공유된 명언")
        }
        context.startActivity(Intent.createChooser(intent, "텍스트로 공유"))
    }
}
