import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.FileProvider
import com.ssafy.glim.BuildConfig
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
        val jsonData = """
        {
            "text":"${quote.content.replace("\n", " ")}",
            "imageUrl":"${BuildConfig.BASE_URL}/images/${quote.quoteImageName}"
        }
    """.trimIndent()

        val encoded = Base64.encodeToString(jsonData.toByteArray(), Base64.NO_WRAP)
        return """
            [글:림]
${quote.content.replace("\n", " ")}
https://689adfefc7117789ec1a6515--venerable-brigadeiros-b967ae.netlify.app/?quote=${quote.quoteId}&text=${encoded}
    """.trimIndent()
    }

    private fun shareTextOnly(quote: Quote) {
        val shareText = buildShareText(quote)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText.trim())
        }
        context.startActivity(Intent.createChooser(intent, "텍스트로 공유"))
    }
}
