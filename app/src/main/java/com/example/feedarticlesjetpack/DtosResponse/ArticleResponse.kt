package com.example.feedarticlesjetpack.DtosResponse

import com.squareup.moshi.Json

data class ArticleResponse(
    @Json(name = "status")
    val status: String,

    @Json(name = "article")
    val article: ArticleDto?
)


