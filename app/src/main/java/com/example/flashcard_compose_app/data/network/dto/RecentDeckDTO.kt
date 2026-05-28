package com.example.flashcard_compose_app.data.network.dto

data class RecentDeckDTO(
    val id: Int,
    val title: String,
    val authorName: String,
    val totalCards: Int,
    val learnedCards: Int,
    val isPublic: Boolean,
    val lastAccessed: String, // ISO 8601 format
)