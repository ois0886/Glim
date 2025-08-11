import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.FileProvider
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.util.ScreenCaptureManager

class ShareWithImageManager(
    private val context: Context,
) {

    fun shareQuoteWithImage(
        quote: Quote
    ) {
        shareTextOnly(quote)
    }

    private fun buildShareText(quote: Quote): String {
        return """
        Glim 앱에서 더 많은 명언을 만나보세요!
        https://68999f56ccd032dec0465b2b--venerable-brigadeiros-b967ae.netlify.app/?quote=${quote.quoteId}
        
    """.trimIndent()
    }

    private fun shareTextOnly(quote: Quote) {
        val shareText = buildShareText(quote)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Glim에서 공유된 명언")
        }
        context.startActivity(Intent.createChooser(intent, "텍스트로 공유"))
    }
}
