package com.muhammadfahreza.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.muhammadfahreza.storyapp.data.StoryRepository
import com.muhammadfahreza.storyapp.data.pref.UserModel
import com.muhammadfahreza.storyapp.data.pref.UserPreference
import com.muhammadfahreza.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StoryViewModel(
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
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

                // Save stories to DataStore
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

    fun getSession(): Flow<UserModel> = userPreference.getSession()

    fun logout() {
        viewModelScope.launch { userPreference.clearSession() }
    }
}
