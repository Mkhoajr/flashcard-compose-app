package com.example.flashcard_compose_app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.data.network.RetrofitClient
import com.example.flashcard_compose_app.data.repository.DeckRepository
import com.example.flashcard_compose_app.data.repository.FlashcardRepository

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {

            val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application

            val authManager = AuthManager(application)

            val apiService = RetrofitClient.deckApiService

            val repository = DeckRepository(apiService)

            HomeViewModel(
                repository = repository,
                authManager = authManager
            )
        }
    }

    val UnitManagerFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer {

            val apiService = RetrofitClient.flashcardApiService
            val repository = FlashcardRepository(apiService)

            UnitManagerViewModel(
                repository = repository
            )
        }
    }

    val FlashcardFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer {

            val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application

            val authManager = AuthManager(application)

            val apiService = RetrofitClient.flashcardApiService

            val repository = FlashcardRepository(apiService)

            FlashcardViewModel(
                repository = repository,
                authManager = authManager
            )
        }
    }
}