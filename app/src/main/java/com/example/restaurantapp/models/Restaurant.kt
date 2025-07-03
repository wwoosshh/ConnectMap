package com.example.restaurantapp.models

data class Restaurant(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phone: String = "",
    val cuisineType: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val rating: Float = 0.0f,
    val createdAt: String? = null
)