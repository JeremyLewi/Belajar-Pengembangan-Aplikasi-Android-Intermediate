package com.example.storyapp.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.model.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreference) : ViewModel() {

    private val _storyLocation = MutableLiveData<StoryResponse>()
    val storyLocation: LiveData<StoryResponse> = _storyLocation

    fun getStoryLocation(token: String) {
        val client = ApiConfig.getApiService().getAllStoryLocation("Bearer $token", 1)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>, response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    _storyLocation.value = response.body()
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e("MapsViewModel", "onFailure: ${t.message}")
            }

        })
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }
}