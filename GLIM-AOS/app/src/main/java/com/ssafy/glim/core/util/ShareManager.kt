package com.ssafy.glim.core.util

import android.util.Base64
import com.ssafy.glim.core.domain.model.Quote
import javax.inject.Inject

class ShareManager @Inject constructor() {

    fun buildDeepLink(quote: Quote): String {
        val encoded = Base64.encodeToString(
            quote.content.toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP
        )
        val url = """
            [글:림]${"\n" + quote.content.replace("\n", " ").trim() + "\n"}https://689f2dad665855e54987c273--venerable-brigadeiros-b967ae.netlify.app/?quote=${quote.quoteId}&text=$encoded"
        """
        return url
    }
}
