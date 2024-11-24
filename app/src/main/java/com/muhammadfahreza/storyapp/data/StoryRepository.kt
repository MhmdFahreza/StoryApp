package com.muhammadfahreza.storyapp.data

import com.muhammadfahreza.storyapp.data.pref.UserPreference
import com.muhammadfahreza.storyapp.data.response.StoryResponse
import com.muhammadfahreza.storyapp.data.response.UploadResponse
import com.muhammadfahreza.storyapp.data.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun getStories(token: String, page: Int, size: Int): StoryResponse {
        return apiService.getStories("Bearer $token", page, size)
    }

    suspend fun uploadStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        token: String
    ): UploadResponse {
        return apiService.uploadStory(photo, description, token)
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
