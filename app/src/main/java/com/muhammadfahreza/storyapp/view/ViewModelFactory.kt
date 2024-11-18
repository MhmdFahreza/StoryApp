package com.muhammadfahreza.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muhammadfahreza.storyapp.data.StoryRepository
import com.muhammadfahreza.storyapp.data.UserRepository
import com.muhammadfahreza.storyapp.data.pref.UserPreference
import com.muhammadfahreza.storyapp.data.pref.dataStore
import com.muhammadfahreza.storyapp.di.Injection
import com.muhammadfahreza.storyapp.view.login.LoginViewModel
import com.muhammadfahreza.storyapp.view.main.StoryViewModel
import com.muhammadfahreza.storyapp.view.signup.SignupViewModel

class ViewModelFactory private constructor(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(storyRepository, userPreference) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            val userRepository = Injection.provideUserRepository(context)
            val storyRepository = Injection.provideStoryRepository(context)
            val userPreference = UserPreference.getInstance(context.dataStore)
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    userRepository,
                    storyRepository,
                    userPreference
                ).also {
                    INSTANCE = it
                }
            }
        }
    }
}
