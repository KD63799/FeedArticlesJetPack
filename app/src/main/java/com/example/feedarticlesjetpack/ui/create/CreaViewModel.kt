package com.example.feedarticlesjetpack.ui.create

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exoremote.DbMethods.RemoteRepository
import com.example.feedarticlesjetpack.DtosResponse.NewArticleDto
import com.example.feedarticlesjetpack.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreaViewModel @Inject constructor(
    @ApplicationContext val context: Context,
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
                _userMessageLiveData.value = context.getString(R.string.invalid_token_or_id)
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
                _userMessageLiveData.value = context.getString(R.string.article_added)
                _navigationDestination.value = R.id.action_creaArticleFragment_to_mainFragment
            } else {
                _userMessageLiveData.value = context.getString(R.string.add_article_failed)
            }
        }
    }
}
