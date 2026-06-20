package com.example.flashcard_compose_app.data.network.dto.request

data class QuizSubmitRequest(
    val userId: Int,
    val deckId: Int,
    val totalQuestions: Int,
    val correctAnswer: Int,
    val answers: List<AnswerDTO>
)

data class AnswerDTO(
    val flashcardId: Int,
    val questionType: String,
    val userAnswer: String,
    val isCorrect: Boolean
)