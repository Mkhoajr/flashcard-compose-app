package com.example.flashcard_compose_app.data.network.api

import com.example.flashcard_compose_app.data.network.dto.FlashcardDTO
import com.example.flashcard_compose_app.data.network.dto.StudyProgressRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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
}