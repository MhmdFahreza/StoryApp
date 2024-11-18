package com.muhammadfahreza.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun fetchStories(token: String, page: Int? = null, size: Int? = null) {
        viewModelScope.launch {
            try {
                val response = storyRepository.getStories(token, page, size)
                _stories.value = response.listStory?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun logout() {
        viewModelScope.launch {
            userPreference.clearSession()
        }
    }
}
