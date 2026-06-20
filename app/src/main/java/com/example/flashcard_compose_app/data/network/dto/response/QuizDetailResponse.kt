package com.example.flashcard_compose_app.data.network.dto.response

import com.google.gson.annotations.SerializedName

data class QuizDetailResponse(
    val flashcardId: Int,
    val word: String,
    val meaning: String,
    val userAnswer: String,
    @field:SerializedName("isCorrect")
    val isCorrect: Boolean
)