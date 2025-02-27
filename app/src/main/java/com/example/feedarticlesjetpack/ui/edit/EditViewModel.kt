package com.example.feedarticlesjetpack.ui.edit

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exoremote.DbMethods.RemoteRepository
import com.example.feedarticlesjetpack.DtosResponse.ArticleDto
import com.example.feedarticlesjetpack.DtosResponse.UpdateArticleDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.SharedPreferences
import com.example.feedarticlesjetpack.R
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class EditViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val remoteRepository: RemoteRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _article = MutableLiveData<ArticleDto>()
    val article: LiveData<ArticleDto> get() = _article

    // LiveData pour la catégorie sélectionnée (1 = Sport, 2 = Manga, 3 = Divers)
    private val _selectedCategory = MutableLiveData<Int?>()
    val selectedCategory: LiveData<Int?>
        get() = _selectedCategory

    // LiveData pour les messages d'erreur ou de succès
    private val _messageLiveData = MutableLiveData<String>()
    val messageLiveData: LiveData<String>
        get() = _messageLiveData


    private val _navigationDestination = MutableLiveData<Int>()
    val navigationDestination: LiveData<Int>
        get() = _navigationDestination


    fun setArticle(article: ArticleDto) {
        _article.value = article
        _selectedCategory.value = article.categorie
    }

    fun setSelectedCategory(category: Int?) {
        _selectedCategory.value = category
    }

    fun updateArticle(title: String, content: String, imageUrl: String) {
        val category = _selectedCategory.value
        if (title.isBlank() || content.isBlank() || imageUrl.isBlank() || category == null || category == 0) {
            _messageLiveData.value = context.getString(R.string.please_fill_all_field_and_category)
            return
        }
        viewModelScope.launch {
            val token = sharedPreferences.getString("token", null)
            if (token == null) {
                _messageLiveData.value = context.getString(R.string.invalid_token)
                return@launch
            }
            val currentArticle = _article.value ?: return@launch
            val updateDto = UpdateArticleDto(
                id = currentArticle.id,
                title = title,
                desc = content,
                image = imageUrl,
                cat = category
            )
            val success = remoteRepository.updateRemoteArticle(updateDto, token)
            if (success) {
                _article.value = currentArticle.copy(
                    titre = title,
                    descriptif = content,
                    url_image = imageUrl,
                    categorie = category
                )
                _messageLiveData.value = context.getString(R.string.article_update)
                _navigationDestination.value = R.id.action_editArticleFragment_to_mainFragment
            } else {
                _messageLiveData.value = context.getString(R.string.updated_error)
            }
        }
    }

    fun deleteArticle() {
        viewModelScope.launch {
            val token = sharedPreferences.getString("token", null)
            val currentArticle = _article.value ?: return@launch
            if (token == null) {
                _messageLiveData.value = context.getString(R.string.invalid_token)
                return@launch
            }
            val success = remoteRepository.deleteRemoteArticle(currentArticle.id, token)
            if (success) {
                _messageLiveData.value = context.getString(R.string.article_deleted)
            } else {
                _messageLiveData.value = context.getString(R.string.error_delete)
            }
        }
    }
}
