package com.example.flashcard_compose_app.domain.model

data class QuizHistory(
    val id: Int,
    val deckTitle: String,
    val totalQuestions: Int,
    val correctAnswer: Int,
    val displayDate: String
)