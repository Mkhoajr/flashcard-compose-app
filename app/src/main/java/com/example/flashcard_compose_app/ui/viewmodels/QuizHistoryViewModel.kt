package com.example.flashcard_compose_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.data.repository.QuizRepository
import com.example.flashcard_compose_app.domain.model.QuizDetail
import com.example.flashcard_compose_app.domain.model.QuizHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizHistoryViewModel(
    private val quizRepository: QuizRepository,
    authManager: AuthManager
) : ViewModel() {

    private val currentUserId = authManager.getUserId()?.toIntOrNull() ?: 0

    // State manage history quiz list of current user
    private val _historyList = MutableStateFlow<List<QuizHistory>>(emptyList())
    val historyList: StateFlow<List<QuizHistory>> = _historyList.asStateFlow()

    // State manage quiz detail of selected quiz from history list
    private val _selectedQuizDetails = MutableStateFlow<List<QuizDetail>>(emptyList())
    val selectedQuizDetails: StateFlow<List<QuizDetail>> = _selectedQuizDetails.asStateFlow()

    private val _isHistoryLoading = MutableStateFlow(false)
    val isHistoryLoading: StateFlow<Boolean> = _isHistoryLoading.asStateFlow()

    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading: StateFlow<Boolean> = _isDetailLoading.asStateFlow()

    fun loadUserHistory() {
        viewModelScope.launch {
            _isHistoryLoading.value = true
            val result = quizRepository.getUserQuizHistory(currentUserId)
            result.onSuccess { list ->
                _historyList.value = list
            }.onFailure {
                println("Log: error to load History quiz List -> ${it.message}")
            }
            _isHistoryLoading.value = false
        }
    }

    fun loadQuizDetails(quizId: Int) {
        viewModelScope.launch {
            _isDetailLoading.value = true
            _selectedQuizDetails.value = emptyList()
            val result = quizRepository.getQuizDetails(quizId)
            result.onSuccess { details ->
                _selectedQuizDetails.value = details
            }.onFailure {
                println("Log: error to load detail quiz -> ${it.message}")
            }
            _isDetailLoading.value = false
        }
    }
}