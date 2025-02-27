package com.example.feedarticlesjetpack.ui.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exoremote.DbMethods.RemoteRepository
import com.example.feedarticlesjetpack.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
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

    fun checkLoginForm(login: String, password: String) {
        if (login.isBlank() || password.isBlank()) {
            _userMessageLiveData.value = context.getString(R.string.please_fill_all_fields)
            return
        }
        viewModelScope.launch {
            delay(2000)
            try {
                val authResponse = remoteRepository.loginRemoteUser(login, password)
                val message = authResponse?.let {
                    when (it.status) {
                        1 -> {
                            sharedPreferences.edit()
                                .putString("token", it.token)
                                .putInt("user_id", it.id)
                                .apply()
                            _navigationDestination.value = R.id.action_loginFragment_to_mainFragment
                            context.getString(R.string.login_sucess)
                        }

                        5 -> context.getString(R.string.login_sucess_token_wrong)
                        0 -> context.getString(R.string.unknow_user)
                        -1 -> context.getString(R.string.parameter_issue)
                        else -> "Unknown error (status: ${it.status})."
                    }
                } ?: context.getString(R.string.connection_error_or_invalid_response)
                _userMessageLiveData.value = message
            } catch (e: Exception) {
                _userMessageLiveData.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}
