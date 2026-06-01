package com.example.flashcard_compose_app.data.repository

import com.example.flashcard_compose_app.data.network.api.FlashcardApiService
import com.example.flashcard_compose_app.data.network.dto.request.FlashcardRequest
import com.example.flashcard_compose_app.data.network.dto.request.StudyProgressRequest
import com.example.flashcard_compose_app.domain.model.Flashcard

class FlashcardRepository(private val apiService: FlashcardApiService) {

    // CREATE NEW FLASHCARD
    suspend fun createFlashcard(request: FlashcardRequest): Result<Boolean> {
        return try {
            val response = apiService.createFlashcard(request)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("error when Create Flashcard: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE FLASHCARD
    suspend fun updateFlashcard(id: Int, request: FlashcardRequest): Result<Boolean> {
        return try {
            val response = apiService.updateFlashcard(id, request)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("error when Update Flashcard: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE FLASHCARD
    suspend fun deleteFlashcard(id: Int): Result<Boolean> {
        return try {
            val response = apiService.deleteFlashcard(id)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("error when Delete Flashcard: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get Flashcards for a specific Deck/Unit
    suspend fun getFlashcards(deckId: Int, userId: Int, unit: String? = null): Result<List<Flashcard>> {
        return try {
            val response = apiService.getFlashcards(deckId, userId, unit)
            if (response.isSuccessful) {
                val flashcards = response.body()?.map { dto ->
                    Flashcard(
                        id = dto.id,
                        unit = dto.unit?: "",
                        word = dto.word,
                        reading = dto.reading,
                        meaning = dto.meaning,
                        imagePath = dto.imagePath,
                        audioPath = dto.audioPath,
                        status = dto.status ?: "not-learned"
                    )
                } ?: emptyList()
                Result.success(flashcards)
            } else {
                Result.failure(Exception("error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudyProgress(userId: Int, flashcardId: Int, status: String) {
        try {
            val request = StudyProgressRequest(userId, flashcardId, status)
            apiService.updateStudyProgress(request)
    }   catch (e: Exception) {
            e.printStackTrace()
        }
    }
}