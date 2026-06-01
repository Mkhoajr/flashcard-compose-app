package com.example.flashcard_compose_app.data.network.dto.request

data class FlashcardRequest(
    val deckId: Int,
    val unit: String?,
    val word: String,
    val reading: String,
    val meaning: String,
    val imagePath: String? = null,
    val audioPath: String? = null,
    val orderIndex: Int = 0
)