package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.common.extensions.parseHtmlString
import com.ssafy.glim.core.data.dto.response.BookDetailResponse
import com.ssafy.glim.core.data.dto.response.BookResponse
import com.ssafy.glim.core.domain.model.Book

fun BookResponse.toDomain(): Book {
    return Book(
        adult = adult,
        author = author,
        categoryId = categoryId,
        categoryName = categoryName,
        cover = coverUrl.replace("/coversum/", "/cover500/"),
        description = description.parseHtmlString(),
        isbn = isbn,
        isbn13 = isbn13,
        link = linkUrl,
        priceSales = priceSales,
        priceStandard = priceStandard,
        pubDate = publishedDate,
        publisher = publisher,
        title = title.parseHtmlString(),
        translator = translator ?: ""
    )
}

fun List<BookResponse>.toDomain(): List<Book> {
    return map { it.toDomain() }
}

fun BookDetailResponse.toDomain() = Book(
    bookId = bookId,
    adult = false,
    author = author,
    categoryId = categoryId,
    categoryName = categoryName,
    cover = coverUrl.replace("/coversum/", "/cover500/"),
    description = description.parseHtmlString(),
    isbn = isbn,
    isbn13 = isbn13,
    link = linkUrl,
    priceSales = 0,
    priceStandard = 0,
    pubDate = publishedDate,
    publisher = publisher,
    title = title.parseHtmlString(),
    views = views,
)
