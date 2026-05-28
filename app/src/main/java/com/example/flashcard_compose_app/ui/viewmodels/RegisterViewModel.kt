package com.example.flashcard_compose_app.ui.viewmodels

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val authManager: AuthManager
) : ViewModel() {

    // Manage UI State
    var email by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Logic handling registration event
    fun onSignUpClick(onSuccess: () -> Unit) {

        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "Please fill in all fields!"
            return
        }

        if (username.length < 3) {
            errorMessage = "Username must be at least 3 characters!"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Invalid email format. Example: user@domain.com"
            return
        }

        if (password != confirmPassword) {
            errorMessage = "Passwords do not match!"
            return
        }

        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters!"
            return
        }

        // Valid data, starting registration process
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val result = authRepository.register(email, username, password, confirmPassword)
                result.fold(
                    onSuccess = { user ->
                        // Assuming registration assigns LEARNER role by default
                        authManager.saveLoginState(user.id.toString(), user.email,user.name, user.role.name)
                        onSuccess()
                    },
                    onFailure = { exception ->
                        errorMessage = exception.message ?: "Registration failed. Please try again."
                    }
                )

            } catch (e: Exception) {
                errorMessage = e.message ?: "Registration failed. Please try again."
            } finally {
                isLoading = false // Stop loading indicator regardless of success or failure
            }
        }
    }
}