package com.example.feedarticlesjetpack.ViewModels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exoremote.DbMethods.RemoteRepository
import com.example.feedarticlesjetpack.DtosResponse.ArticleDto
import com.example.feedarticlesjetpack.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _allArticles = MutableLiveData<List<ArticleDto>>()

    private val _articlesLiveData = MutableLiveData<List<ArticleDto>>()
    val articlesLiveData: LiveData<List<ArticleDto>>
        get() = _articlesLiveData

    private val _navigationDestination = MutableLiveData<Int>()
    val navigationDestination: LiveData<Int>
        get() = _navigationDestination

    private val _userMessageLiveData = MutableLiveData<String>()
    val userMessageLiveData: LiveData<String>
        get() = _userMessageLiveData

    private val _selectedCategory = MutableLiveData<Int?>()
    val selectedCategory: LiveData<Int?>
        get() = _selectedCategory

    private var favoritesFilterEnabled: Boolean = false

    init {
        loadArticles()
    }

    fun loadArticles() {
        viewModelScope.launch {
            val token = sharedPreferences.getString("token", null)
            if (token == null) {
                _userMessageLiveData.value = "Token invalide, veuillez vous reconnecter."
                _navigationDestination.value = R.id.action_mainFragment_to_loginFragment
                return@launch
            }
            val articles = remoteRepository.getRemoteArticles(token)
            _allArticles.value = articles
            applyFilters()
        }
    }

    private fun applyFilters() {
        val category = _selectedCategory.value
        val filtered = _allArticles.value?.filter { article ->
            (category == null || article.categorie == category) &&
                    (!favoritesFilterEnabled || article.is_fav == 1)
        }
        _articlesLiveData.value = filtered ?: emptyList()
    }

    fun setSelectedCategory(category: Int?) {
        _selectedCategory.value = category
        applyFilters()
    }

    fun setFavoritesFilter(enabled: Boolean) {
        favoritesFilterEnabled = enabled
        applyFilters()
    }

    fun logOff() {
        sharedPreferences.edit().remove("token").remove("user_id").apply()
        _navigationDestination.value = R.id.action_mainFragment_to_loginFragment
    }

    fun navigateToAddArticle() {
        _navigationDestination.value = R.id.action_mainFragment_to_creaArticleFragment
    }

    fun isOwner(article: ArticleDto): Boolean {
        val userId = sharedPreferences.getInt("user_id", -1)
        return article.id_u == userId
    }
}
