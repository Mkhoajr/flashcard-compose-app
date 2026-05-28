package com.example.flashcard_compose_app.data.network.dto

data class DeckDTO(
    val id: Int,
    val title: String,
    val authorName: String,
    val totalCards: Int,
    val learnedCards: Int,
    val isPublic: Boolean,
    val isUnit: Boolean = false,
    val parentId: Int? = null,
)