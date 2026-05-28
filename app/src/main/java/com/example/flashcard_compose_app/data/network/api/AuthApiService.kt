package com.example.flashcard_compose_app.data.network.api

import com.example.flashcard_compose_app.data.network.dto.LoginRequest
import com.example.flashcard_compose_app.data.network.dto.LoginResponse
import com.example.flashcard_compose_app.data.network.dto.RegisterRequest
import com.example.flashcard_compose_app.data.network.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Endpoint to communicate with backend Server
interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}