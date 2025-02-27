package com.example.feedarticlesjetpack.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exoremote.DbMethods.RemoteRepository
import com.example.feedarticlesjetpack.DtosResponse.NewArticleDto
import com.example.feedarticlesjetpack.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreaViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val sharedPreferences: android.content.SharedPreferences
) : ViewModel() {

    private val _userMessageLiveData = MutableLiveData<String>()
    val userMessageLiveData: LiveData<String>
        get() = _userMessageLiveData

    private val _navigationDestination = MutableLiveData<Int>()
    val navigationDestination: LiveData<Int>
        get() = _navigationDestination

    fun addArticle(title: String, content: String, imageUrl: String, category: Int) {
        viewModelScope.launch {
            val token = sharedPreferences.getString("token", null)
            val userId = sharedPreferences.getInt("user_id", -1)
            if (token == null || userId == -1) {
                _userMessageLiveData.value = "Token ou ID invalide, veuillez vous reconnecter."
                _navigationDestination.value = R.id.action_creaArticleFragment_to_loginFragment
                return@launch
            }

            val newArticleDto = NewArticleDto(
                id_u = userId,
                title = title,
                desc = content,
                image = imageUrl,
                cat = category
            )

            val success = remoteRepository.addRemoteArticle(newArticleDto, token)

            if (success) {
                _userMessageLiveData.value = "Article ajouté avec succès."
                _navigationDestination.value = R.id.action_creaArticleFragment_to_mainFragment
            } else {
                _userMessageLiveData.value = "Échec de l'ajout de l'article."
            }
        }
    }
}
