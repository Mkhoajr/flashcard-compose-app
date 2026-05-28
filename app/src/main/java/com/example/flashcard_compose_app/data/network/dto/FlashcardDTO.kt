package com.example.flashcard_compose_app.data.network.dto

data class FlashcardDTO(
    val id: Int,
    val unit: String,
    val word: String,
    val reading: String,
    val meaning: String,
    val imagePath: String?, // May be null
    val audioPath: String?,  // May be null
    val status: String? // "LEARNING", "KNEW", "not-learned" or null if not set
)