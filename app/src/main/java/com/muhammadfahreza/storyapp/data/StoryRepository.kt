package com.muhammadfahreza.storyapp.data

import com.muhammadfahreza.storyapp.data.pref.UserPreference
import com.muhammadfahreza.storyapp.data.response.StoryResponse
import com.muhammadfahreza.storyapp.data.retrofit.ApiService

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun getStories(token: String, page: Int? = null, size: Int? = null): StoryResponse {
        return apiService.getStories("Bearer $token", page, size, location = 1)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference).also { instance = it }
            }
        }
    }
}