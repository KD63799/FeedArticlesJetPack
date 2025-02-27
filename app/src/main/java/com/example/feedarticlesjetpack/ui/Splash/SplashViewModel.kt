package com.example.feedarticlesjetpack.ui.Splash


import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.feedarticlesjetpack.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _destination = MutableLiveData<Int>()
    val destination: LiveData<Int> get() = _destination

    fun checkToken() {
        val token = sharedPreferences.getString("token", null)
        _destination.value = if (token != null) {
            R.id.action_splashFragment_to_mainFragment
        } else {
             R.id.action_splashFragment_to_loginFragment
        }
    }
}
