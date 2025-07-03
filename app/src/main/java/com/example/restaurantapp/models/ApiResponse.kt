package com.example.restaurantapp.models

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String = "",
    val error: String? = null
)