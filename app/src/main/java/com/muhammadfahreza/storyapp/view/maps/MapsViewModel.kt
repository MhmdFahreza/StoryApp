package com.muhammadfahreza.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.muhammadfahreza.storyapp.data.StoryRepository
import com.muhammadfahreza.storyapp.data.response.ListStoryItem
import com.muhammadfahreza.storyapp.data.response.StoryResponse

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStoriesWithLocation() : LiveData<List<ListStoryItem>> = liveData {
        try {
            val response: StoryResponse = storyRepository.getStoriesWithLocation()
            val listStory = response.listStory?.filterNotNull()
                ?.filter { it.lat != null && it.lon != null } ?: emptyList()
            emit(listStory)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
