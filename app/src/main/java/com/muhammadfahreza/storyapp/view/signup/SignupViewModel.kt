// SignupViewModel.kt
package com.muhammadfahreza.storyapp.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammadfahreza.storyapp.data.UserRepository
import com.muhammadfahreza.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerResult = MutableStateFlow<Result<RegisterResponse>?>(null)
    val registerResult: StateFlow<Result<RegisterResponse>?> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            userRepository.register(name, email, password).collect { result ->
                _registerResult.value = result
            }
        }
    }
}
