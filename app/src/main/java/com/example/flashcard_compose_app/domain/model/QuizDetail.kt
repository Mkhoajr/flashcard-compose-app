package com.example.flashcard_compose_app.domain.model

data class QuizDetail(
    val flashcardId: Int,
    val word: String,
    val correctAnswer: String, // Mình đổi tên một chút từ 'meaning' cho UI dễ hiểu logic hơn
    val userAnswer: String,
    val isCorrect: Boolean
)