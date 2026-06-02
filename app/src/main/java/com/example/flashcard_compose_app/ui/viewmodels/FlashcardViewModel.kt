package com.example.flashcard_compose_app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.data.network.dto.FlashcardDTO
import com.example.flashcard_compose_app.data.repository.FlashcardRepository
import com.example.flashcard_compose_app.domain.model.Flashcard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class FlashcardAction {
    KNEW, STILL_LEARNING
}

class FlashcardViewModel(
    private val repository: FlashcardRepository,
    authManager: AuthManager
) : ViewModel() {


    private val currentUserId = authManager.getUserId()?.toIntOrNull() ?: 0

    // Flashcard Data
    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    // Current Card Index (Dùng cho thanh Progress Bar: 1/10, 2/10...)
    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex: StateFlow<Int> = _currentCardIndex.asStateFlow()

    // Deck and Unit Names
    private val _deckName = MutableStateFlow<String>("")
    val deckName: StateFlow<String> = _deckName.asStateFlow()

    private val _unitTitle = MutableStateFlow<String>("")
    val unitTitle: StateFlow<String> = _unitTitle.asStateFlow()

    private val _unitName = MutableStateFlow<String>("")
    val unitName: StateFlow<String> = _unitName.asStateFlow()

    // Learned Count (Green - Knew)
    private val _learnedCount = MutableStateFlow(0)
    val learnedCount: StateFlow<Int> = _learnedCount.asStateFlow()

    // Still Learning Count (Orange - Still Learning)
    private val _stillLearningCount = MutableStateFlow(0)
    val stillLearningCount: StateFlow<Int> = _stillLearningCount.asStateFlow()

    // Flip State
    private val _isFlipped = MutableStateFlow(false)
    val isFlipped: StateFlow<Boolean> = _isFlipped.asStateFlow()

    // Favorites Set
    private val _favorites = MutableStateFlow<Set<Int>>(emptySet())
    val favorites: StateFlow<Set<Int>> = _favorites.asStateFlow()

    // Loading State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val actionHistory = java.util.ArrayDeque<FlashcardAction>()
    private val learnedCardsInSession = mutableSetOf<String>()

    fun loadFlashcards(deckId: Int, unitId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Receive List<Flashcard> from Repository
                val result = repository.getFlashcards(deckId, currentUserId, unitId)

                result.onSuccess { flashcardList ->

                    _flashcards.value = flashcardList

                    _favorites.value = flashcardList
                        .filter { it.isFavourite == true }
                        .map { it.id }
                        .toSet()

                    if (flashcardList.isNotEmpty()) {
                        _unitName.value = unitId ?: flashcardList.first().unit
                    }

                    val resumeIndex = flashcardList.indexOfFirst {
                        it.status.equals("not-learned", ignoreCase = true) || it.status.equals("NOT_LEARNED", ignoreCase = true)
                    }

                    if (resumeIndex != -1) {
                        _currentCardIndex.value = resumeIndex
                    } else {
                        _currentCardIndex.value = flashcardList.size
                    }

                    _isFlipped.value = false
                    learnedCardsInSession.clear()
                    actionHistory.clear()

                    _learnedCount.value = flashcardList.count { it.status.equals("LEARNED", ignoreCase = true) }
                    _stillLearningCount.value = flashcardList.count { it.status.equals("STILL_LEARNING", ignoreCase = true) }
                }
                result.onFailure { error ->
                    println("Error loading flashcards: ${error.message}")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setDeckName(name: String) { _deckName.value = name }
    fun setUnitTitle(title: String) { _unitTitle.value = title }
    fun toggleFlip() { _isFlipped.value = !_isFlipped.value }

    fun toggleFavorite(cardId: Int) {
        // Update UI immediately for better UX
        val currentFavorites = _favorites.value.toMutableSet()
        if (currentFavorites.contains(cardId)) {
            currentFavorites.remove(cardId)
        } else {
            currentFavorites.add(cardId)
        }
        _favorites.value = currentFavorites

        viewModelScope.launch {
            val result = repository.toggleFavorite(cardId)
            if (result.isFailure) {
                println("DEBUG: error to toggle Favourite - ${result.exceptionOrNull()?.message}")
            }
        }
    }

    // Function to Update Local Card Status After User Action (Knew / Still Learning)
    private fun updateLocalCardStatus(cardId: Int, newStatus: String) {
        _flashcards.value = _flashcards.value.map { card ->
            if (card.id == cardId) card.copy(status = newStatus) else card
        }
    }

    // Knew button
    fun markCardAsLearned() {

        val card = _flashcards.value.getOrNull(_currentCardIndex.value)

        card?.let {

            actionHistory.addLast(FlashcardAction.KNEW)
            updateBackendProgress(it.id, "LEARNED")

            updateLocalCardStatus(it.id, "LEARNED")

            _learnedCount.value++
            _currentCardIndex.value++
            _isFlipped.value = false
        }
    }

    // Still Learning button
    fun markCardAsNotLearned() {

        val card = _flashcards.value.getOrNull(_currentCardIndex.value)

        card?.let {

            actionHistory.addLast(FlashcardAction.STILL_LEARNING)
            updateBackendProgress(it.id, "STILL_LEARNING")

            updateLocalCardStatus(it.id, "STILL_LEARNING")

            _stillLearningCount.value++
            _currentCardIndex.value++
            _isFlipped.value = false
        }
    }

    // UNDO button
    fun undoLastAction() {

        if (_currentCardIndex.value > 0 && actionHistory.isNotEmpty()) {

            val lastAction = actionHistory.removeLast()
            val previousCardIndex = _currentCardIndex.value - 1
            val cardToUndo = _flashcards.value.getOrNull(previousCardIndex)

            if (lastAction == FlashcardAction.KNEW && _learnedCount.value > 0) {
                _learnedCount.value--
            } else if (lastAction == FlashcardAction.STILL_LEARNING && _stillLearningCount.value > 0) {
                _stillLearningCount.value--
            }

            cardToUndo?.let {

                // Update local status back to "NOT_LEARNED" and optionally update backend as well
                updateLocalCardStatus(it.id, "NOT_LEARNED")

                // Update backend to reflect the undo action (set status back to "not-learned")
                updateBackendProgress(it.id, "not-learned")
            }

            _currentCardIndex.value--
            _isFlipped.value = false
        }
    }


    private fun updateBackendProgress(flashcardId: Int, status: String) {
        viewModelScope.launch {
            try {
                val response = repository.updateStudyProgress(currentUserId, flashcardId, status)
                Log.d("FlashcardViewModel", "Update Result: $response")
            } catch (e: Exception) {
                Log.e("FlashcardViewModel", "Failed to update progress", e)
            }
        }
    }


    fun focusOnStillLearning() {

        val cardsToRelearn = _flashcards.value.filter {
            it.status.equals("STILL_LEARNING", ignoreCase = true)
        }

        // 2. Cập nhật lại danh sách flashcards chỉ gồm những thẻ này
        _flashcards.value = cardsToRelearn

        // 3. QUAN TRỌNG: Không reset các biến đếm về 0 nếu muốn cộng dồn vào con số 15
        // Hoặc nếu muốn reset, bạn phải hiểu đây là hiệp học mới cho 11 thẻ
        _currentCardIndex.value = 0
        _isFlipped.value = false

        // Nếu bạn muốn Summary hiện: Know 15, Still learning 11 (ban đầu)
        // thì không được reset _learnedCount về 0 ở đây.
        // _learnedCount.value = 0  <-- Bỏ dòng này nếu muốn giữ số 15

        _stillLearningCount.value = 0
        actionHistory.clear()
    }

    // Func for "Restart Flashcards" button
    fun restartAll() {
        // Iterate through all flashcards and send reset command to Backend
        _flashcards.value.forEach { card ->
            // Only reset those that are not already in "NOT_LEARNED"
            if (!card.status.equals("NOT_LEARNED", ignoreCase = true)) {
                updateBackendProgress(card.id, "not-learned")
            }
        }

        // 2. Reset status to NOT_LEARNED cho danh sách Local
        _flashcards.value = _flashcards.value.map {
            it.copy(status = "NOT_LEARNED")
        }

        // 3. Reset toàn bộ bộ đếm trên UI
        _currentCardIndex.value = 0
        _isFlipped.value = false
        _learnedCount.value = 0
        _stillLearningCount.value = 0
        actionHistory.clear()
    }
}