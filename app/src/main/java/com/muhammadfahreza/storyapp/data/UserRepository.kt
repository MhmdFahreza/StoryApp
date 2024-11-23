package com.muhammadfahreza.storyapp.data

import com.muhammadfahreza.storyapp.data.pref.UserModel
import com.muhammadfahreza.storyapp.data.pref.UserPreference
import com.muhammadfahreza.storyapp.data.response.LoginResponse
import com.muhammadfahreza.storyapp.data.response.RegisterResponse
import com.muhammadfahreza.storyapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    fun register(name: String, email: String, password: String): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.register(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun login(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.login(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun clearLoginStatus() {
        userPreference.clearSession()
        userPreference.clearToken()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
