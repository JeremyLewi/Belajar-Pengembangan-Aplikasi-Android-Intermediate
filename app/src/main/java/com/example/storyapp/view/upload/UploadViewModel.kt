package com.example.storyapp.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.remote.response.NewStoryResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.model.UserPreference
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _uploadStory = MutableLiveData<NewStoryResponse>()
    val uploadStory: LiveData<NewStoryResponse> = _uploadStory

    fun uploadStory(
        token: String,
        description: RequestBody,
        file: MultipartBody.Part,
        lat: Double?,
        lon: Double?
    ) {
        _isLoading.value = true
        val requestLat = lat?.toString()?.toRequestBody("text/plain".toMediaType())
        val requestLon = lon?.toString()?.toRequestBody("text/plain".toMediaType())
        val client = ApiConfig.getApiService().uploadStory("Bearer $token", description, file, requestLat, requestLon)
        client.enqueue(object : Callback<NewStoryResponse> {
            override fun onResponse(
                call: Call<NewStoryResponse>, response: Response<NewStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _uploadStory.value = response.body()
                }
            }

            override fun onFailure(call: Call<NewStoryResponse>, t: Throwable) {
                _isLoading.value = false
            }

        })
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

}