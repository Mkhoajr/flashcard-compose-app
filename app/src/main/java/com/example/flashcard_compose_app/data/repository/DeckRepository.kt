package com.example.flashcard_compose_app.data.repository

import com.example.flashcard_compose_app.data.network.api.DeckApiService
import com.example.flashcard_compose_app.data.network.dto.request.DeckRequest
import com.example.flashcard_compose_app.domain.model.Deck

class DeckRepository(private val apiService: DeckApiService) {

    // Get all root decks
    suspend fun getRootDecks(userId: Int): Result<List<Deck>> {
        return try {
            val response = apiService.getAllDecks(userId)
            if (response.isSuccessful) {
                // Map DTO -> Domain Model
                val decks = response.body()?.map { dto ->
                    Deck(
                        id = dto.id,
                        title = dto.title,
                        author = dto.authorName,
                        learnedCards = dto.learnedCards,
                        totalCards = dto.totalCards,
                    )
                } ?: emptyList()
                Result.success(decks)
            } else {
                Result.failure(Exception("error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentDecks(userId: Int): Result<List<Deck>> {
        return try {
            val response = apiService.getRecentDecks(userId)
            if (response.isSuccessful) {
                val decks = response.body()?.map { dto ->
                    Deck(
                        id = dto.id,
                        title = dto.title,
                        author = dto.authorName,
                        learnedCards = dto.learnedCards,
                        totalCards = dto.totalCards,
                    )
                } ?: emptyList()
                Result.success(decks)
            } else {
                Result.failure(Exception("error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get sub-decks for a specific parent deck
    suspend fun getSubDecks(deckId: Int, userId: Int): Result<List<Deck>> {
        return try {
            val response = apiService.getSubDecks(deckId, userId)
            if (response.isSuccessful) {
                val decks = response.body()?.map { dto ->
                    Deck(
                        id = dto.id,
                        title = dto.title,
                        author = dto.authorName,
                        learnedCards = dto.learnedCards,
                        totalCards = dto.totalCards,
                        isUnit = dto.isUnit,
                        parentId = deckId,
                    )
                } ?: emptyList()
                Result.success(decks)
            } else {
                Result.failure(Exception("error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get units for a specific deck
    suspend fun getUnitsByDeckId(deckId: Int, userId: Int): Result<List<Deck>> {
        return try {
            val response = apiService.getUnitsByDeckId(deckId, userId)
            if (response.isSuccessful) {
                val units = response.body()?.mapIndexed { index, dto ->
                    Deck(
                        id = "${deckId}-${index}".hashCode(),  // Generate unique ID from parent + index
                        title = dto.unit,
                        author = "", // Units don't have author
                        learnedCards = dto.learnedCards,
                        totalCards = dto.totalCards,
                        isUnit = true,
                        parentId = deckId,
                    )
                } ?: emptyList()
                Result.success(units)
            } else {
                Result.failure(Exception("error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Create a new root deck (CREATE)
    suspend fun createDeck(request: DeckRequest): Result<Boolean> {
        return try {
            val response = apiService.createDeck(request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("error Create Deck: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE
    suspend fun updateDeck(id: Int, request: DeckRequest): Result<Boolean> {
        return try {
            val response = apiService.updateDeck(id, request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("error Update Deck: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE
    suspend fun deleteDeck(id: Int): Result<Boolean> {
        return try {
            val response = apiService.deleteDeck(id)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("error Delete Deck: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}