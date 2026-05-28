package com.example.flashcard_compose_app.domain.model

data class Deck(
    val id: Int,
    val title: String,
    val author: String = "",
    val learnedCards: Int = 0,
    val totalCards: Int = 0,
    val parentId: Int? = null, // null for root decks
    val isUnit : Boolean = false, // true if this deck is a unit (not a real deck, just a grouping)
    val subDecks: List<Deck> = emptyList() // for expandable
)