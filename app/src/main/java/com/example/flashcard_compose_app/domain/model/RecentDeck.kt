package com.example.flashcard_compose_app.domain.model

data class RecentDeck(
    val deck: Deck,
    val lastLearned: Long // timestamp
)
