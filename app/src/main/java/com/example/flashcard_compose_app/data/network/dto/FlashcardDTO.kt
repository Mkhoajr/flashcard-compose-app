package com.example.flashcard_compose_app.data.network.dto

data class FlashcardDTO(
    val id: Int,
    val word: String,
    val reading: String,
    val meaning: String,
    val imagePath: String?,
    val audioPath: String?,
    val unit: String?,
    val orderIndex: Int,
    val status: String?,
    val isFavourite: Boolean? = false
)