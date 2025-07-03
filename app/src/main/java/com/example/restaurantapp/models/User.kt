package com.example.restaurantapp.models

data class User(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val createdAt: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)