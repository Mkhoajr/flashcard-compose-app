package com.example.flashcard_compose_app.data.network.dto.response

import com.google.gson.annotations.SerializedName

data class QuizHistoryResponse(
    val quizId: Int,
    val deckTitle: String,
    val totalQuestions: Int,
    val correctAnswer: Int,
    @SerializedName("quizDate")
    val quizDate: String
)