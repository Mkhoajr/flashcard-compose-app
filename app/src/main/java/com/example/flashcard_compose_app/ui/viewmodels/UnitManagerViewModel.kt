package com.example.flashcard_compose_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard_compose_app.data.network.dto.request.FlashcardRequest
import com.example.flashcard_compose_app.data.repository.FlashcardRepository
import com.example.flashcard_compose_app.domain.model.Flashcard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface UnitUiState {
    object Loading : UnitUiState
    data class Success(val flashcards: List<Flashcard>) : UnitUiState
    data class Error(val message: String) : UnitUiState
}

class UnitManagerViewModel(private val repository: FlashcardRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UnitUiState>(UnitUiState.Loading)
    val uiState: StateFlow<UnitUiState> = _uiState

    fun addFlashcard(
        deckId: Int,
        userId: Int,
        unitTitle: String?,
        word: String,
        reading: String,
        meaning: String,
        imagePath: String? = null,
        audioPath: String? = null
    ) {
        viewModelScope.launch {

            val request = FlashcardRequest(deckId, unitTitle, word, reading, meaning, imagePath, audioPath)
            val result = repository.createFlashcard(request)
            if (result.isSuccess) {
                loadFlashcards(deckId, userId, unitTitle) // Refresh UI
            }
        }
    }

    fun updateFlashcard(
        cardId: Int,
        deckId: Int,
        userId: Int,
        unitTitle: String?,
        word: String,
        reading: String,
        meaning: String,
        imagePath: String? = null,
        audioPath: String? = null
    ) {
        viewModelScope.launch {

            val request = FlashcardRequest(deckId, unitTitle, word, reading, meaning, imagePath, audioPath)
            val result = repository.updateFlashcard(cardId, request)
            if (result.isSuccess) {
                loadFlashcards(deckId, userId, unitTitle) // Refresh UI
            }
        }
    }

    fun deleteFlashcard(cardId: Int, deckId: Int, userId: Int, unitTitle: String?) {
        viewModelScope.launch {

            val result = repository.deleteFlashcard(cardId)
            if (result.isSuccess) {
                loadFlashcards(deckId, userId, unitTitle) // Refresh UI
            }
        }
    }

    fun loadFlashcards(deckId: Int, userId: Int, unitTitle: String?) {

        viewModelScope.launch {

            _uiState.value = UnitUiState.Loading

            val result = repository.getFlashcards(deckId, userId, unitTitle)

            result.onSuccess { flashcards ->
                println("DEBUG: API Success - Received ${flashcards.size} flashcards")
                _uiState.value = UnitUiState.Success(flashcards)
            }.onFailure { exception ->
                println("DEBUG: API Failure - ${exception.message}")
                _uiState.value = UnitUiState.Error(exception.message ?: "err to connect to Server")
            }
        }
    }

    fun loadFavoriteFlashcards(userId: Int) {
        viewModelScope.launch {

            _uiState.value = UnitUiState.Loading

            val result = repository.getFavoriteFlashcards(userId)

            result.onSuccess { flashcards ->
                _uiState.value = UnitUiState.Success(flashcards)
            }.onFailure { exception ->
                _uiState.value = UnitUiState.Error(exception.message ?: "Could not load favorite flashcards!")
            }
        }
    }

    fun toggleFavoriteStatus(cardId: Int, userId: Int) {
        viewModelScope.launch {

            val result = repository.toggleFavorite(cardId)

            if (result.isSuccess) {
                loadFavoriteFlashcards(userId)
            } else {
                println("DEBUG: Failed to toggle favorite status")
            }
        }
    }
}