package com.example.storyapp.data.remote.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.storyapp.data.StoryRemoteMediator
import com.example.storyapp.data.remote.database.StoryDatabase
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.retrofit.ApiService

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStory(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}