package com.muhammadfahreza.storyapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.muhammadfahreza.storyapp.data.pref.UserPreference
import com.muhammadfahreza.storyapp.data.response.ListStoryItem
import com.muhammadfahreza.storyapp.data.response.StoryResponse
import com.muhammadfahreza.storyapp.data.response.UploadResponse
import com.muhammadfahreza.storyapp.data.retrofit.ApiService
import com.muhammadfahreza.storyapp.view.main.StoryPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun getPagedStories(token: String): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).flow
    }

    suspend fun getStories(token: String, page: Int, size: Int): StoryResponse {
        return apiService.getStories("Bearer $token", page, size)
    }

    suspend fun uploadStory(
        photo: MultipartBody.Part,
        description: RequestBody
    ): UploadResponse {
        val token = userPreference.getSession().first().token
        return apiService.uploadStory(photo, description, "Bearer $token")
    }

    suspend fun getStoriesWithLocation(): StoryResponse {
        return apiService.getStoriesWithLocation()
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
