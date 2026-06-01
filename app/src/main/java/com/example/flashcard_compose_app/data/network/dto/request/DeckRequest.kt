package com.example.flashcard_compose_app.data.network.dto.request

data class DeckRequest(
    val title: String,
    val description: String,
    val authorId: Int,
    val parentDeckId: Int? = null,
    val isUnit: Boolean = false,
    val isPublic: Boolean
)