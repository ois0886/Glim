package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.BookResponse
import com.ssafy.glim.core.domain.model.Book

fun BookResponse.toDomain(): Book {
    return Book(
        adult = adult,
        author = author,
        categoryId = categoryId,
        categoryName = categoryName,
        cover = coverUrl,
        description = description,
        fixedPrice = fixedPrice,
        isbn = isbn,
        isbn13 = isbn13,
        itemId = itemId,
        link = linkUrl,
        priceSales = priceSales,
        priceStandard = priceStandard,
        pubDate = publishedDate,
        publisher = publisher,
        stockStatus = stockStatus,
        title = title,
        translator = translator ?: ""
    )
}

fun List<BookResponse>.toDomain(): List<Book> {
    return map { it.toDomain() }
}
