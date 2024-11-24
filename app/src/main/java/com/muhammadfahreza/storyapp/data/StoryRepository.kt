package com.muhammadfahreza.storyapp.data

import com.muhammadfahreza.storyapp.data.pref.UserPreference
import com.muhammadfahreza.storyapp.data.response.StoryResponse
import com.muhammadfahreza.storyapp.data.response.UploadResponse
import com.muhammadfahreza.storyapp.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun getStories(token: String, page: Int, size: Int): StoryResponse {
        return apiService.getStories("Bearer $token", page, size)
    }

    suspend fun uploadStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody
    ): UploadResponse {
        return apiService.uploadStory(
            photo = photo,
            description = description,
            lat = "0".toRequestBody("text/plain".toMediaType()),
            lon = "0".toRequestBody("text/plain".toMediaType()),
            headers = mapOf("Authorization" to token)
        )
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