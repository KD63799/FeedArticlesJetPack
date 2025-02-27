package com.example.feedarticlesjetpack.ui.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exoremote.DbMethods.RemoteRepository
import com.example.feedarticlesjetpack.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    @ApplicationContext val context: Context,
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
            _userMessageLiveData.value = context.getString(R.string.please_fill_all_fields)
            return
        }
        if (password != confirmPassword) {
            _userMessageLiveData.value = context.getString(R.string.passwords_dont_match)
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
                        _userMessageLiveData.value =
                            context.getString(R.string.registration_success)

                        _navigationDestination.value = R.id.action_registerFragment_to_mainFragment

                    }
                    5 -> _userMessageLiveData.value =
                        context.getString(R.string.login_in_use)

                    0 -> _userMessageLiveData.value =
                        context.getString(R.string.registration_failed)

                    -1 -> _userMessageLiveData.value =
                        context.getString(R.string.parameter_issue)

                    else -> _userMessageLiveData.value =
                        "Erreur inconnue (status: ${registerResponse.status})"
                }
            } else {
                _userMessageLiveData.value = context.getString(R.string.error_connection)
            }
        }
    }
}
