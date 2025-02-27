package com.example.feedarticlesjetpack.DtosResponse


import com.squareup.moshi.Json

data class DeleteArticleDto(
    @Json(name = "status")
    val status: Int
)