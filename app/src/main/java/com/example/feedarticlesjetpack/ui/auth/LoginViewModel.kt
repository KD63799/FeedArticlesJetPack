package com.example.feedarticlesjetpack.ui.auth

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exoremote.DbMethods.RemoteRepository
import com.example.feedarticlesjetpack.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _userMessageLiveData = MutableLiveData<String>()
    val userMessageLiveData: LiveData<String>
        get() = _userMessageLiveData

    private val _navigationDestination = MutableLiveData<Int>()
    val navigationDestination: LiveData<Int>
        get() = _navigationDestination

    fun checkLoginForm(login: String, password: String) {
        if (login.isNotEmpty() && password.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    delay(2000)
                    val authResponse = remoteRepository.loginRemoteUser(login, password)
                    if (authResponse != null) {
                        when (authResponse.status) {
                            1 -> {
                                authResponse.token?.let { token ->
                                    sharedPreferences.edit()
                                        .putString("token", authResponse.token)
                                        .putInt("user_id", authResponse.id)
                                        .apply()
                                }
                                _userMessageLiveData.value = "Authentification réussie."

                                _navigationDestination.value =
                                    R.id.action_loginFragment_to_mainFragment
                            }
                            5 -> _userMessageLiveData.value = "Authentification réussie mais token inchangé."
                            0 -> _userMessageLiveData.value = "Utilisateur inconnu (login/mdp incorrects)."
                            -1 -> _userMessageLiveData.value = "Problème de paramètre."
                            else -> _userMessageLiveData.value = "Erreur inconnue (status: ${authResponse.status})."
                        }
                    } else {
                        _userMessageLiveData.value = "Erreur de connexion ou réponse invalide."
                    }
                } catch (e: Exception) {
                    _userMessageLiveData.value = "Erreur: ${e.localizedMessage}"
                }
            }
        } else {
            _userMessageLiveData.value = "Veuillez remplir tous les champs."
        }
    }
}
