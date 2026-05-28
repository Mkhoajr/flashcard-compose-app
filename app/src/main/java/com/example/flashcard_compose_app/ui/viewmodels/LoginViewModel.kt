package com.example.flashcard_compose_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.domain.model.Role
import com.example.flashcard_compose_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authManager: AuthManager, private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {

            _loginState.value = LoginState.Loading

            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->

                    if (user.role == Role.LEARNER) {
                        authManager.saveLoginState(user.id.toString(), user.email, user.name, user.role.name)
                        _loginState.value = LoginState.Success
                    } else {
                        _loginState.value = LoginState.Error("Wrong Account User. Please check again!")
                    }
                },
                onFailure = { exception ->

                    val errorMessage = if (exception.message?.contains("401") == true) {
                        "Wrong Email or Password. Please check again!"
                    } else {
                        exception.message ?: "Login failed. Please check again!"
                    }

                    _loginState.value = LoginState.Error(errorMessage)
                }
            )
        }
    }
}
