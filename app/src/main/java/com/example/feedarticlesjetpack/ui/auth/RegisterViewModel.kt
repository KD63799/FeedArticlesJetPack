package com.example.feedarticlesjetpack.ui.auth

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exoremote.DbMethods.RemoteRepository
import com.example.feedarticlesjetpack.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _userMessageLiveData = MutableLiveData<String>()
    val userMessageLiveData: LiveData<String>
        get() = _userMessageLiveData

    private val _navigationDestination = MutableLiveData<Int>()
    val navigationDestination: LiveData<Int>
        get() = _navigationDestination

    fun checkRegisterForm(login: String, password: String, confirmPassword: String) {
        if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _userMessageLiveData.value = "Veuillez remplir tous les champs"
            return
        }
        if (password != confirmPassword) {
            _userMessageLiveData.value = "Les mots de passe ne correspondent pas"
            return
        }
        viewModelScope.launch {
            val registerResponse = remoteRepository.registerRemoteUser(login, password)
            if (registerResponse != null) {
                when (registerResponse.status) {
                    1 -> {
                        registerResponse.token?.let { token ->
                            sharedPreferences.edit().putString("token", token).apply()
                        }
                        _userMessageLiveData.value = "Inscription réussie"

                        _navigationDestination.value = R.id.action_registerFragment_to_mainFragment

                    }
                    5 -> _userMessageLiveData.value = "Login déjà utilisé"
                    0 -> _userMessageLiveData.value = "Inscription non réalisée"
                    -1 -> _userMessageLiveData.value = "Problème de paramètre"
                    else -> _userMessageLiveData.value = "Erreur inconnue (status: ${registerResponse.status})"
                }
            } else {
                _userMessageLiveData.value = "Erreur de connexion"
            }
        }
    }
}
