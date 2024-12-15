package com.muhammadfahreza.storyapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.muhammadfahreza.storyapp.data.StoryRepository
import com.muhammadfahreza.storyapp.data.UserRepository
import com.muhammadfahreza.storyapp.data.pref.UserModel
import com.muhammadfahreza.storyapp.data.pref.UserPreference
import com.muhammadfahreza.storyapp.data.response.ListStoryItem
import com.muhammadfahreza.storyapp.data.response.UploadResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()

    private val gson = Gson()

    private fun fetchStories(token: String, page: Int = 1, size: Int = 10) {
        viewModelScope.launch {
            try {
                val response = storyRepository.getStories(token, page, size)
                val stories = response.listStory?.filterNotNull() ?: emptyList()
                _stories.value = stories
                saveStoriesToDataStore(stories)
            } catch (e: Exception) {
                Log.e("ERROR", "Failed to fetch stories: ${e.message}")
            }
        }
    }


    private suspend fun saveStoriesToDataStore(stories: List<ListStoryItem>) {
        val storiesJson = gson.toJson(stories)
        userPreference.saveStories(storiesJson)
    }

    fun loadStoriesFromDataStore(skipCache: Boolean = false) {
        viewModelScope.launch {
            if (skipCache) {
                getSession().collect { user ->
                    if (user.token.isNotEmpty()) {
                        fetchStories(user.token)
                    }
                }
            } else {
                val storiesJson = userPreference.getStories()
                if (storiesJson.isNotEmpty()) {
                    val type = object : TypeToken<List<ListStoryItem>>() {}.type
                    val cachedStories: List<ListStoryItem> = gson.fromJson(storiesJson, type)
                    _stories.postValue(cachedStories)
                } else {
                    getSession().collect { user ->
                        if (user.token.isNotEmpty()) {
                            fetchStories(user.token)
                        }
                    }
                }
            }
        }
    }


    fun uploadStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part
    ): LiveData<Result<UploadResponse>> {
        val result = MutableLiveData<Result<UploadResponse>>()
        viewModelScope.launch {
            try {
                if (token.isNotEmpty()) {
                    val response = storyRepository.uploadStory(photo, description)
                    result.postValue(Result.success(response))

                    fetchStories(token, page = 1, size = 10)
                } else {
                    result.postValue(Result.failure(Exception("Token kosong")))
                }
            } catch (e: Exception) {
                result.postValue(Result.failure(e))
                if (e is retrofit2.HttpException && e.code() == 401) {
                    clearLoginStatus()
                }
            }
        }
        return result
    }

    fun getStories(token: String): Flow<PagingData<ListStoryItem>> {
        return storyRepository.getPagedStories(token).cachedIn(viewModelScope)
    }

    fun getSession(): Flow<UserModel> = userPreference.getSession()

    fun clearLoginStatus() {
        viewModelScope.launch {
            userRepository.clearLoginStatus()
        }
    }
}
