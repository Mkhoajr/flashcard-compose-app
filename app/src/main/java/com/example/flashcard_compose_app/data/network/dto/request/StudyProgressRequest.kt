package com.example.flashcard_compose_app.data.network.dto.request

data class StudyProgressRequest(
    val userId: Int,
    val flashcardId: Int,
    val status: String // "LEARNING", "KNEW", "not-learned"
)