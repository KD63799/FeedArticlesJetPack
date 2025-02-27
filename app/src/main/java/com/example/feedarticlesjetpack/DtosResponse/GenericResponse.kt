package com.example.feedarticlesjetpack.DtosResponse

import com.squareup.moshi.Json

data class GenericResponse(
    @Json(name="status")
    val status: Int
)
