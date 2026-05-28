package com.example.flashcard_compose_app.data.network.dto

import com.example.flashcard_compose_app.domain.model.Role
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val confirmPassword: String
)

data class RegisterResponse(
    @SerializedName("userId")
    val id: Int,

    val username: String,
    val email: String,

    @SerializedName("role")
    val roleString: String,

    val message: String
) {
    val role: Role
        get() = when (roleString.trim().uppercase()) {
            "LEARNER" -> Role.LEARNER
            "ADMIN" -> Role.ADMIN
            else -> throw IllegalArgumentException("Invalid Role!")
        }
}

data class LoginResponse(
    @SerializedName("userId")
    val id: Int,

    val username: String,
    val email: String,

    @SerializedName("role")
    val roleString: String,

    val message: String
) {
    val role: Role
        get() = when (roleString.trim().uppercase()) {
            "LEARNER" -> Role.LEARNER
            "ADMIN" -> Role.ADMIN
            else -> throw IllegalArgumentException("Invalid Role!")
        }
}