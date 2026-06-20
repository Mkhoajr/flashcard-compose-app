package com.example.flashcard_compose_app.data.network

import com.example.flashcard_compose_app.data.network.api.AuthApiService
import com.example.flashcard_compose_app.data.network.api.DeckApiService
import com.example.flashcard_compose_app.data.network.api.FlashcardApiService
import com.example.flashcard_compose_app.data.network.api.QuizApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/api/" // Server URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val deckApiService: DeckApiService = retrofit.create(DeckApiService::class.java)
    val flashcardApiService: FlashcardApiService = retrofit.create(FlashcardApiService::class.java)
    val quizApiService: QuizApiService = retrofit.create(QuizApiService::class.java)
}