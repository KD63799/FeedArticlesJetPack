package com.example.feedarticlesjetpack.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlesjetpack.DtosResponse.ArticleDto
import com.example.exoremote.DbMethods.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import java.util.Locale

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _article = MutableLiveData<ArticleDto>()
    val article: LiveData<ArticleDto> get() = _article

    fun setArticle(article: ArticleDto) {
        _article.value = article
    }

    fun toggleFavorite() {
        val currentArticle = _article.value ?: return
        val token = sharedPreferences.getString("token", null) ?: return
        viewModelScope.launch {
            val success = remoteRepository.toggleFavorite(currentArticle.id, token)
            if (success) {
                val updatedArticle =
                    currentArticle.copy(is_fav = if (currentArticle.is_fav == 1) 0 else 1)
                _article.value = updatedArticle
            }
        }
    }

    fun getCategoryName(): String {
        return when (_article.value?.categorie) {
            1 -> "Sport"
            2 -> "Manga"
            3 -> "Divers"
            else -> "Inconnue"
        }
    }

    fun convertFormatDate(date: String): String {
        val dateInputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateOutputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val objectDatefromdateStr = dateInputFormat.parse(date)
        return dateOutputFormat.format(objectDatefromdateStr) ?: "not a date"
    }
}
