package com.example.flashcard_compose_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.data.network.dto.request.AnswerDTO
import com.example.flashcard_compose_app.data.network.dto.request.QuizSubmitRequest
import com.example.flashcard_compose_app.data.repository.FlashcardRepository
import com.example.flashcard_compose_app.data.repository.QuizRepository
import com.example.flashcard_compose_app.domain.model.Flashcard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class QuizQuestion(
    val flashcard: Flashcard,
    val correctAnswer: String,
    val options: List<String>
)

class QuizViewModel(
    private val flashcardRepository: FlashcardRepository,
    private val quizRepository: QuizRepository,
    authManager: AuthManager
) : ViewModel() {

    private val currentUserId = authManager.getUserId()?.toIntOrNull() ?: 0
    private var currentDeckId: Int = 0
    private val _answerHistory = mutableListOf<AnswerDTO>()
    private var isSubmitting = false

    // Loading Status
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Question List Created
    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    // Current Question Index
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    // Count of Correct and Incorrect Answers (Point)
    private val _correctCount = MutableStateFlow(0)
    val correctCount: StateFlow<Int> = _correctCount.asStateFlow()

    private val _incorrectCount = MutableStateFlow(0)
    val incorrectCount: StateFlow<Int> = _incorrectCount.asStateFlow()

    // Finished Quiz Status
    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    fun loadQuizData(deckId: Int, unitTitle: String) {

        currentDeckId = deckId
        isSubmitting = false

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Flashcard from Repository
                val result = flashcardRepository.getFlashcards(deckId, currentUserId, unitTitle)

                result.onSuccess { flashcards ->
                    if (flashcards.isNotEmpty()) {
                        generateQuizQuestions(flashcards)
                    } else {
                        _isFinished.value = true
                    }
                }
                result.onFailure {
                    println("Error loading quiz: ${it.message}")
                    _isFinished.value = true
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateQuizQuestions(allCards: List<Flashcard>) {

        _answerHistory.clear()

        // Get all Meaning to make choice answer
        val allMeanings = allCards.map { it.meaning }.distinct()

        // Shuffle Vocab
        val shuffledCards = allCards.shuffled()

        val generatedQuestions = shuffledCards.map { card ->
            val correctAnswer = card.meaning

            // Filter answer DIFFER with correct answer, shuffle, and get 3 wrong answer
            val wrongAnswers = allMeanings
                .filter { it != correctAnswer }
                .shuffled()
                .take(3)

            // Mix 1 correct answer and 3 wrong answer together -> Shuffle Position
            val finalOptions = (listOf(correctAnswer) + wrongAnswers).shuffled()

            QuizQuestion(
                flashcard = card,
                correctAnswer = correctAnswer,
                options = finalOptions
            )
        }

        _quizQuestions.value = generatedQuestions
        _currentIndex.value = 0
        _correctCount.value = 0
        _incorrectCount.value = 0
        _isFinished.value = false
    }

    // When user select an answer, we check if it's correct or not, then move to next question
    fun submitAnswer(selectedAnswer: String) {
        val currentQuestion = _quizQuestions.value.getOrNull(_currentIndex.value) ?: return
        val isCorrect = selectedAnswer == currentQuestion.correctAnswer

        if (isCorrect) _correctCount.value += 1 else _incorrectCount.value += 1

        _answerHistory.add(
            AnswerDTO(
                flashcardId = currentQuestion.flashcard.id,
                questionType = "multiple choice",
                userAnswer = selectedAnswer,
                isCorrect = isCorrect
            )
        )

        moveToNextQuestion()
    }

    // User can skip the question, which counts as incorrect and moves to the next question
    fun skipQuestion() {
        val currentQuestion = _quizQuestions.value.getOrNull(_currentIndex.value) ?: return

        _incorrectCount.value += 1

        // When User don't know -> The answer will blank or "SKIPPED", isCorrect = false
        _answerHistory.add(
            AnswerDTO(
                flashcardId = currentQuestion.flashcard.id,
                questionType = "multiple choice",
                userAnswer = "SKIPPED",
                isCorrect = false
            )
        )

        moveToNextQuestion()
    }

    private fun moveToNextQuestion() {
        if (_currentIndex.value < _quizQuestions.value.size - 1) {
            _currentIndex.value += 1
        } else {
            submitQuizToServer()
        }
    }

    private fun submitQuizToServer() {

        if (isSubmitting) return // Prevent multiple submissions

        isSubmitting = true

        viewModelScope.launch {
            val request = QuizSubmitRequest(
                userId = currentUserId,
                deckId = currentDeckId,
                totalQuestions = _quizQuestions.value.size,
                correctAnswer = _correctCount.value,
                answers = _answerHistory.toList()
            )

            try {
                val response = quizRepository.submitQuizHistory(request)
                if (response.isSuccess) {
                    println("Submit success to Server!")
                } else {
                    println("Submit err: ${response.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // when err or success, display result screen
                _isFinished.value = true
            }
        }
    }

    fun restartQuiz() {
        // Reset all state to initial
        isSubmitting = false
        generateQuizQuestions(_quizQuestions.value.map { it.flashcard })
    }
}