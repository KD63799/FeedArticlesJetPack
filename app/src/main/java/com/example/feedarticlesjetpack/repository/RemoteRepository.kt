package com.example.exoremote.DbMethods

import android.content.ContentValues.TAG
import android.util.Log
import com.example.exoremote.Network.ApiInterface
import com.example.feedarticlesjetpack.DtosResponse.ArticleDto
import com.example.feedarticlesjetpack.DtosResponse.ArticleResponse
import com.example.feedarticlesjetpack.DtosResponse.ArticlesResponse
import com.example.feedarticlesjetpack.DtosResponse.AuthResponse
import com.example.feedarticlesjetpack.DtosResponse.GenericResponse
import com.example.feedarticlesjetpack.DtosResponse.NewArticleDto
import com.example.feedarticlesjetpack.DtosResponse.RegisterDto
import com.example.feedarticlesjetpack.DtosResponse.UpdateArticleDto


import javax.inject.Inject
import retrofit2.Response



class RemoteRepository @Inject constructor(
    private val apiInterface : ApiInterface
) {

    suspend fun loginRemoteUser(login: String, password: String): AuthResponse? {
        Log.d("RemoteRepository", "loginRemoteUser called with: $login / $password")
        return try {
            val response: Response<AuthResponse>? = apiInterface?.login(login, password)
            if (response!!.isSuccessful) {
                Log.d("RemoteRepository", "Response: ${response.body()}")
                response.body()
            } else {
                Log.e("RemoteRepository", "Unsuccessful response: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "Erreur lors du login", e)
            null
        }
    }

    suspend fun registerRemoteUser(login: String, password: String): AuthResponse? {
        return try {
            val response: Response<AuthResponse> =
                apiInterface.register(RegisterDto(login = login, mdp = password))
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("RemoteRepository", "Erreur lors du login", e)
            null
        }
    }

    suspend fun getRemoteArticles(token: String): List<ArticleDto> {
        return try {
            val response: Response<ArticlesResponse>? =
                apiInterface.getAllArticles(withFav = 1, token = token)
            if (response!!.isSuccessful) {
                response.body()?.articles ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRemoteArticle(id: Int, token: String): ArticleDto? {
        return try {
            val response: Response<ArticleResponse>? =
                apiInterface.getArticle(id = id, withFav = 1, token = token)
            if (response!!.isSuccessful) response.body()?.article else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addRemoteArticle(article: NewArticleDto, token: String): Boolean {
        return try {
            val response =
                apiInterface.addArticle(newArticleDto = article, token = token)
            response!!.isSuccessful && response.body()?.status == 1
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateRemoteArticle(article: UpdateArticleDto, token: String): Boolean {
        return try {
            val response: Response<GenericResponse>? =
                apiInterface.updateArticle(
                    id = article.id,
                    updateArticleDto = article,
                    token = token
                )
            response!!.isSuccessful && response.body()?.status == 1
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteRemoteArticle(id: Int, token: String): Boolean {
        return try {
            val response: Response<GenericResponse>? =
                apiInterface.deleteArticle(id = id, token = token)
            response!!.isSuccessful && response.body()?.status == 1
        } catch (e: Exception) {
            false
        }
    }

    suspend fun toggleFavorite(articleId: Int, token: String): Boolean {
        return try {
            val response = apiInterface.toggleFavorite(articleId, token)
            response!!.isSuccessful && response.body()?.status == 1
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors du toggle favorite", e)
            false
        }
    }

}


