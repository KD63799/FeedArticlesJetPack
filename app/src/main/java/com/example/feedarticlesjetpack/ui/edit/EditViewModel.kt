package com.example.feedarticlesjetpack.ui.edit

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

@HiltViewModel
class EditViewModel @Inject constructor(
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
        // Initialisez la catégorie sélectionnée à celle de l'article
        _selectedCategory.value = article.categorie
    }

    fun setSelectedCategory(category: Int?) {
        _selectedCategory.value = category
    }

    fun updateArticle(title: String, content: String, imageUrl: String) {
        val category = _selectedCategory.value
        if (title.isBlank() || content.isBlank() || imageUrl.isBlank() || category == null || category == 0) {
            _messageLiveData.value = "Veuillez remplir tous les champs et sélectionner une catégorie."
            return
        }
        viewModelScope.launch {
            val token = sharedPreferences.getString("token", null)
            if (token == null) {
                _messageLiveData.value = "Token invalide, veuillez vous reconnecter."
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
                _messageLiveData.value = "Article mis à jour avec succès."
                _navigationDestination.value = R.id.action_editArticleFragment_to_mainFragment
            } else {
                _messageLiveData.value = "Erreur lors de la mise à jour de l'article."
            }
        }
    }

    fun deleteArticle() {
        viewModelScope.launch {
            val token = sharedPreferences.getString("token", null)
            val currentArticle = _article.value ?: return@launch
            if (token == null) {
                _messageLiveData.value = "Token invalide, veuillez vous reconnecter."
                return@launch
            }
            val success = remoteRepository.deleteRemoteArticle(currentArticle.id, token)
            if (success) {
                _messageLiveData.value = "Article supprimé."
            } else {
                _messageLiveData.value = "Erreur lors de la suppression de l'article."
            }
        }
    }

    // Optionnel : fonction pour récupérer le libellé de la catégorie
    fun getCategoryName(): String {
        return when (_selectedCategory.value) {
            1 -> "Sport"
            2 -> "Manga"
            3 -> "Divers"
            else -> "Inconnue"
        }
    }
}
