package com.example.feedarticlesjetpack.DtosResponse

import com.squareup.moshi.Json

data class AuthResponse(
    @Json(name ="status")
    val status: Int,

    @Json(name ="id")
    val id: Int,

    @Json(name ="token")
    val token: String?
)

