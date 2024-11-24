package com.muhammadfahreza.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val stories: LiveData<List<ListStoryItem>> get() = _stories

    private val gson = Gson()

    fun fetchStories(token: String, page: Int? = null, size: Int? = null) {
        viewModelScope.launch {
            try {
                val response = storyRepository.getStories(token, page, size)
                val storyList = response.listStory?.filterNotNull() ?: emptyList()
                _stories.value = storyList

                saveStoriesToDataStore(storyList)
            } catch (e: Exception) {
                e.printStackTrace()
                loadStoriesFromDataStore()
            }
        }
    }

    private suspend fun saveStoriesToDataStore(stories: List<ListStoryItem>) {
        val storiesJson = gson.toJson(stories)
        userPreference.saveStories(storiesJson)
    }

    private fun loadStoriesFromDataStore() {
        viewModelScope.launch {
            val storiesJson = userPreference.getStories()
            if (storiesJson.isNotEmpty()) {
                val type = object : TypeToken<List<ListStoryItem>>() {}.type
                val cachedStories: List<ListStoryItem> = gson.fromJson(storiesJson, type)
                _stories.postValue(cachedStories)
            }
        }
    }

    fun uploadStory(
        token: String,
        description: RequestBody,
        image: MultipartBody.Part
    ): LiveData<Result<UploadResponse>> {
        val result = MutableLiveData<Result<UploadResponse>>()
        viewModelScope.launch {
            try {
                val response = storyRepository.uploadStory(token, image, description)
                result.postValue(Result.success(response))
            } catch (e: Exception) {
                result.postValue(Result.failure(e))
            }
        }
        return result
    }

    fun getSession(): Flow<UserModel> = userPreference.getSession()

    fun clearLoginStatus() {
        viewModelScope.launch {
            userRepository.clearLoginStatus()
        }
    }
}
