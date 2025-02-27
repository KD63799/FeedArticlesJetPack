package com.example.feedarticlesjetpack.DtosResponse

import com.squareup.moshi.Json

data class ArticlesResponse(
    @Json(name = "status")
    val status: String,

    @Json(name = "articles")
    val articles: List<ArticleDto>
)