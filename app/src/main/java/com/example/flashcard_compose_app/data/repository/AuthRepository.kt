package com.example.flashcard_compose_app.data.repository

import User
import com.example.flashcard_compose_app.data.network.api.AuthApiService
import com.example.flashcard_compose_app.data.network.dto.LoginRequest
import com.example.flashcard_compose_app.data.network.dto.RegisterRequest

class AuthRepository(private val authApiService: AuthApiService) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {

            // Request login to backend server and get response
            val response = authApiService.login(LoginRequest(email, password))

            // Check if response is successful
            if (response.isSuccessful) {
                val loginResponse = response.body()

                if (loginResponse != null) {

                    // Map data from loginResponse to User model
                    val user = User(
                        id = loginResponse.id,
                        name = loginResponse.username,
                        email = loginResponse.email,
                        role = loginResponse.role
                    )

                    Result.success(user)

                } else {
                    Result.failure(Exception("Null response body"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, username: String, password: String, confirmPassword: String): Result<User> {
        return try {
            val response = authApiService.register(RegisterRequest(email, username, password, confirmPassword))

            if (response.isSuccessful) {
                val registerResponse = response.body()

                if (registerResponse != null) {

                    val user = User(
                        id = registerResponse.id,
                        name = registerResponse.username,
                        email = registerResponse.email,
                        role = registerResponse.role
                    )

                    Result.success(user)

                } else {
                    Result.failure(Exception("Null response body"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
