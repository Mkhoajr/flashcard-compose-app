package com.example.flashcard_compose_app.domain.model

data class Flashcard(
    val id: Int,
    val unit: String,
    val word: String,
    val reading: String,
    val meaning: String,
    val imagePath: String?, // Maybe null
    val audioPath: String?, // Maybe null
    val status: String
)