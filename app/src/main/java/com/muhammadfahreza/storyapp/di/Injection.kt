// Injection.kt
package com.muhammadfahreza.storyapp.di

import android.content.Context
import com.muhammadfahreza.storyapp.data.UserRepository
import com.muhammadfahreza.storyapp.data.pref.UserPreference
import com.muhammadfahreza.storyapp.data.pref.dataStore
import com.muhammadfahreza.storyapp.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(userPreference, apiService)
    }
}
