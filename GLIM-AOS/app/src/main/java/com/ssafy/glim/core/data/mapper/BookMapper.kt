package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.BookResponse
import com.ssafy.glim.core.domain.model.Book

fun BookResponse.toDomain(): Book {
    return Book(
        adult = adult,
        author = author,
        categoryId = categoryId,
        categoryName = categoryName,
        cover = cover,
        description = description,
        fixedPrice = fixedPrice,
        isbn = isbn,
        itemId = itemId,
        link = link,
        priceSales = priceSales,
        priceStandard = priceStandard,
        pubDate = pubDate,
        publisher = publisher,
        stockStatus = stockStatus,
        title = title
    )
}

fun List<BookResponse>.toDomain(): List<Book> {
    return map { it.toDomain() }
}
