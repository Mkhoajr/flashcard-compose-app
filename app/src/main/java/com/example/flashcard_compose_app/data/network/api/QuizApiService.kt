package com.example.flashcard_compose_app.data.network.api

import com.example.flashcard_compose_app.data.network.dto.request.QuizSubmitRequest
import com.example.flashcard_compose_app.data.network.dto.response.QuizDetailResponse
import com.example.flashcard_compose_app.data.network.dto.response.QuizHistoryResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface QuizApiService {

    // Submit Quiz
    // @PostMapping("/submit")
    @POST("quizzes/submit")
    suspend fun submitQuizHistory(
        @Body request: QuizSubmitRequest
    ): Response<Map<String, String>> // Backend return Map<String, String>

    // Get Quiz History
    // @GetMapping("/history")
    @GET("quizzes/history")
    suspend fun getQuizHistory(
        @Query("userId") userId: Int
    ): Response<List<QuizHistoryResponse>>

    // Get Quiz Details
    // @GetMapping("/{quizId}/details")
    @GET("quizzes/{quizId}/details")
    suspend fun getQuizDetails(
        @Path("quizId") quizId: Int
    ): Response<List<QuizDetailResponse>>
}