package com.example.storyapp.view.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.remote.paging.StoryRepository
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.model.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference, storyRepository: StoryRepository) :
    ViewModel() {


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    val getAllStory: LiveData<PagingData<Story>> = pref.getToken().asLiveData().switchMap { token ->
        storyRepository.getStory(token).cachedIn(viewModelScope)
    }


    fun getLoginSession(): LiveData<Boolean> {
        return pref.getLoginSession().asLiveData()
    }


    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }


    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}