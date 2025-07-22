package com.ssafy.glim.core.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import com.ssafy.glim.core.data.datasource.impl.QuoteRemoteDataSource
import com.ssafy.glim.core.data.dto.request.BookCreateData
import com.ssafy.glim.core.data.dto.request.QuoteRequest
import com.ssafy.glim.core.data.dto.request.toRequestDto
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Glim
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class QuoteRepositoryImpl @Inject constructor(
    private val quoteRemoteDataSource: QuoteRemoteDataSource,
) : QuoteRepository {

    override fun searchQuotes(query: String): Flow<List<Quote>> {
        TODO("Not yet implemented")
    }

    override fun getGlims(page: Int, size: Int, sort: String): Flow<List<Glim>> {
        TODO("Not yet implemented")
    }

    override fun createGlim(
        content: String,
        bookId: Long,
        book: Book,
        bitmap: Bitmap
    ): Flow<Result<Unit>> = flow {

        val pair = createQuoteMultipartData(
            content = content,
            bookId = bookId,
            bookCreateData = book.toRequestDto(),
            bitmap = bitmap
        )

        val response = quoteRemoteDataSource.createQuote(
            pair.first, pair.second
        )

        if(response.isSuccessful) {
            if(response.body() == null) {
                Log.e("QuoteRepository", "Response body is null")
                emit(Result.failure(Exception("Response body is null")))
                return@flow
            }
            else {
                Log.d("QuoteRepository", "Quote created successfully: ${response.body()}")
                emit(Result.success(Unit))
            }
        }
        else {
            Log.e("QuoteRepository", "Failed to create quote: ${response.errorBody()?.string()}")
            emit(Result.failure(Exception("Failed to create quote")))
        }
    }
}

private fun createQuoteMultipartData(
    content: String,
    bookId: Long,
    bookCreateData: BookCreateData,
    bitmap: Bitmap
): Pair<RequestBody, MultipartBody.Part?> {

    val quoteRequest = QuoteRequest(
        content = content,
        bookId = bookId,
        bookCreateData = bookCreateData
    )
    val jsonRequestBody = quoteRequest.toJsonRequestBody()

    val imagePart = bitmap.toImagePart(
        partName = "quoteImage",
        fileName = "quote.jpg",
        quality = 85
    )

    return Pair(jsonRequestBody, imagePart)
}

private fun Any.toJsonRequestBody(): RequestBody {
    val gson = Gson()
    return gson.toJson(this).toRequestBody("application/json".toMediaTypeOrNull())
}

private fun Bitmap.toImagePart(
    partName: String = "quoteImage",
    fileName: String = "quote.jpg",
    quality: Int = 85
): MultipartBody.Part? {
    return try {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val imageBytes = outputStream.toByteArray()

        val requestBody =
            imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0)
        MultipartBody.Part.createFormData(partName, fileName, requestBody)
    } catch (e: Exception) {
        Log.e("BitmapToImagePart", "Failed to convert bitmap", e)
        null
    }
}