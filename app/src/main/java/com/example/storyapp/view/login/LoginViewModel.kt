package com.example.storyapp.view.login

import androidx.lifecycle.*
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.model.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>, response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    viewModelScope.launch {
                        response.body()?.loginResult?.token?.let { token ->
                            saveToken(token)
                            saveLoginSession(true)
                        }
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    when {
                        errorBody?.contains("User not found") == true -> {
                            _errorMessage.postValue("User not found")
                        }
                        errorBody?.contains("Invalid password") == true -> {
                            _errorMessage.postValue("Invalid password")
                        }
                        else -> {
                            _errorMessage.postValue("An error occurred")
                        }
                    }

                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.postValue(t.message ?: "An error occurred")
            }
        })
    }

    suspend fun saveToken(token: String) {
        pref.saveToken(token)
    }


    suspend fun saveLoginSession(loginSession: Boolean) {
        pref.saveLoginSession(loginSession)
    }

    fun getLoginSession(): LiveData<Boolean> {
        return pref.getLoginSession().asLiveData()
    }


}

