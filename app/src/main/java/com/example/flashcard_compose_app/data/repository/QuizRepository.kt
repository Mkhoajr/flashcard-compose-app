package com.example.flashcard_compose_app.data.repository

import com.example.flashcard_compose_app.data.network.api.QuizApiService
import com.example.flashcard_compose_app.data.network.dto.request.QuizSubmitRequest
import com.example.flashcard_compose_app.domain.model.QuizDetail
import com.example.flashcard_compose_app.domain.model.QuizHistory
import kotlin.collections.map

class QuizRepository(private val apiService: QuizApiService) {

    suspend fun submitQuizHistory(request: QuizSubmitRequest): Result<Boolean> {
        return try {
            val response = apiService.submitQuizHistory(request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to submit quiz: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserQuizHistory(userId: Int): Result<List<QuizHistory>> {
        return try {
            val response = apiService.getQuizHistory(userId)
            if (response.isSuccessful && response.body() != null) {
                val domainList = response.body()!!.map { dto ->
                    QuizHistory(
                        id = dto.quizId,
                        deckTitle = dto.deckTitle,
                        totalQuestions = dto.totalQuestions,
                        correctAnswer = dto.correctAnswer,
                        displayDate = formatQuizDate(dto.quizDate)
                    )
                }
                Result.success(domainList)
            } else {
                Result.failure(Exception("Error fetch history: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuizDetails(quizId: Int): Result<List<QuizDetail>> {
        return try {
            val response = apiService.getQuizDetails(quizId)
            if (response.isSuccessful) {
                // Mapping from List DTO to List Domain Model
                val detailList = response.body()?.map { dto ->
                    QuizDetail(
                        flashcardId = dto.flashcardId,
                        word = dto.word,
                        correctAnswer = dto.meaning, // Map meaning to correctAnswer
                        userAnswer = dto.userAnswer,
                        isCorrect = dto.isCorrect
                    )
                } ?: emptyList()

                Result.success(detailList)
            } else {
                Result.failure(Exception("Failed to load details: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun formatQuizDate(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) return "--/--/----"
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val date = inputFormat.parse(rawDate)
            if (date != null) outputFormat.format(date) else rawDate
        } catch (e: Exception) {
            rawDate
        }
    }
}