package com.muhammadfahreza.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.muhammadfahreza.storyapp.data.UserRepository
import com.muhammadfahreza.storyapp.data.pref.UserModel

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    suspend fun logout() {
        repository.logout()
    }
}
