package com.example.flashcard_compose_app.data.network.api

import com.example.flashcard_compose_app.data.network.dto.FlashcardDTO
import com.example.flashcard_compose_app.data.network.dto.request.FlashcardRequest
import com.example.flashcard_compose_app.data.network.dto.request.StudyProgressRequest
import retrofit2.Response
import retrofit2.http.*
interface FlashcardApiService {

    @GET("flashcards/deck/{deckId}")
    suspend fun getFlashcards(
        @Path("deckId") deckId: Int,
        @Query("userId") userId: Int,
        @Query("unit") unit: String? = null
    ): Response<List<FlashcardDTO>>

    @POST("study/progress")
    suspend fun updateStudyProgress(
        @Body request: StudyProgressRequest
    ): Response<Void>

    @POST("flashcards")
    suspend fun createFlashcard(@Body request: FlashcardRequest): Response<Void>

    @PUT("flashcards/{id}")
    suspend fun updateFlashcard(@Path("id") id: Int, @Body request: FlashcardRequest): Response<Void>

    @DELETE("flashcards/{id}")
    suspend fun deleteFlashcard(@Path("id") id: Int): Response<Void>

}