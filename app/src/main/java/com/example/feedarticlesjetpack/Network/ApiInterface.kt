package com.example.exoremote.Network

import com.example.feedarticlesjetpack.DtosResponse.AuthResponse
import com.example.feedarticlesjetpack.DtosResponse.ArticleResponse
import com.example.feedarticlesjetpack.DtosResponse.ArticlesResponse
import com.example.feedarticlesjetpack.DtosResponse.GenericResponse
import com.example.feedarticlesjetpack.DtosResponse.NewArticleDto
import com.example.feedarticlesjetpack.DtosResponse.RegisterDto
import com.example.feedarticlesjetpack.DtosResponse.UpdateArticleDto
import com.example.feedarticlesjetpack.Network.ApiRoutes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @PUT(ApiRoutes.REGISTER)
    suspend fun register(@Body registerDto: RegisterDto): Response<AuthResponse>

    @FormUrlEncoded
    @POST(ApiRoutes.LOGIN)
    suspend fun login(
        @Field("login") login: String,
        @Field("mdp") mdp: String
    ): Response<AuthResponse>?


    // Récupération de tous les articles
    @GET(ApiRoutes.GET_ALL_ARTICLES)
    suspend fun getAllArticles(
        @Query("with_fav") withFav: Int = 1,
        @Header(ApiRoutes.AUTH_PARAM) token: String
    ): Response<ArticlesResponse>?

    // Récupération d'un article unique
    @GET("articles/{id}")
    suspend fun getArticle(
        @Path("id") id: Int,
        @Query("with_fav") withFav: Int = 1,
        @Header(ApiRoutes.AUTH_PARAM) token: String
    ): Response<ArticleResponse>?

    // Mise à jour d'un article
    @POST("articles/{id}")
    suspend fun updateArticle(
        @Path("id") id: Int,
        @Body updateArticleDto: UpdateArticleDto,
        @Header(ApiRoutes.AUTH_PARAM) token: String
    ): Response<GenericResponse>?

    // Suppression d'un article
    @DELETE("articles/{id}")
    suspend fun deleteArticle(
        @Path("id") id: Int,
        @Header(ApiRoutes.AUTH_PARAM) token: String
    ): Response<GenericResponse>?

    // Création d'un nouvel article
    @PUT(ApiRoutes.ADD_ARTICLE)
    suspend fun addArticle(
        @Body newArticleDto: NewArticleDto,
        @Header(ApiRoutes.AUTH_PARAM) token: String
    ): Response<GenericResponse>?

    // Changement du statut favori d'un article
    @PUT("articles/{id}")
    suspend fun toggleFavorite(
        @Path("id") id: Int,
        @Header(ApiRoutes.AUTH_PARAM) token: String
    ): Response<GenericResponse>?
}
