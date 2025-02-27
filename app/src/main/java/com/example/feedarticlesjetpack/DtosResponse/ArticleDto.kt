package com.example.feedarticlesjetpack.DtosResponse

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleDto(
    @Json(name = "id")
    val id: Int,

    @Json(name = "titre")
    val titre: String,

    @Json(name = "descriptif")
    val descriptif: String,

    @Json(name = "url_image")
    val url_image: String,

    @Json(name = "categorie")
    val categorie: Int,

    @Json(name = "created_at")
    val created_at: String,

    @Json(name = "id_u")
    val id_u: Int,

    @Json(name = "is_fav")
    val is_fav: Int // 1 pour favori, 0 sinon
) : Parcelable
