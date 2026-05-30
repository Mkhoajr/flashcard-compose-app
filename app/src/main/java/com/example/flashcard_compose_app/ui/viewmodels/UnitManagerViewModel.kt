package com.example.flashcard_compose_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}