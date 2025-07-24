package com.ssafy.glim.core.domain.model

import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote

data class CurationContent(
    val book : List<Book>,
    val quote : List<Quote>
)
