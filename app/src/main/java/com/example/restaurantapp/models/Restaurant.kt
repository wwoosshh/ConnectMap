package com.example.restaurantapp.models

import com.google.gson.annotations.SerializedName

data class Restaurant(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("phone")
    val phone: String = "",

    @SerializedName("address")
    val address: String = "",

    @SerializedName("latitude")
    val latitude: Double = 0.0,

    @SerializedName("longitude")
    val longitude: Double = 0.0,

    @SerializedName("cuisineTypeId")
    val cuisineTypeId: Int = 0,

    @SerializedName("openingHours")
    val openingHours: String = "",

    @SerializedName("rating")
    val rating: Float = 0.0f,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("cuisineType")
    val cuisineType: CuisineType? = null,

    @SerializedName("menus")
    val menus: List<Menu> = emptyList(),

    @SerializedName("reviews")
    val reviews: List<Review> = emptyList(),

    @SerializedName("images")
    val images: List<RestaurantImage> = emptyList()
)

data class CuisineType(
    @SerializedName("cuisineTypeId")
    val cuisineTypeId: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("description")
    val description: String = ""
)

data class Menu(
    @SerializedName("menuId")
    val menuId: Int = 0,

    @SerializedName("restaurantId")
    val restaurantId: Int = 0,

    @SerializedName("categoryId")
    val categoryId: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("price")
    val price: Double = 0.0,

    @SerializedName("imageUrl")
    val imageUrl: String = "",

    @SerializedName("isAvailable")
    val isAvailable: Boolean = true,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("category")
    val category: MenuCategory? = null
)

data class MenuCategory(
    @SerializedName("categoryId")
    val categoryId: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("description")
    val description: String = ""
)

data class Review(
    @SerializedName("reviewId")
    val reviewId: Int = 0,

    @SerializedName("userId")
    val userId: Int = 0,

    @SerializedName("restaurantId")
    val restaurantId: Int = 0,

    @SerializedName("rating")
    val rating: Int = 0,

    @SerializedName("comment")
    val comment: String = "",

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("user")
    val user: User? = null
)

data class RestaurantImage(
    @SerializedName("imageId")
    val imageId: Int = 0,

    @SerializedName("restaurantId")
    val restaurantId: Int = 0,

    @SerializedName("imageUrl")
    val imageUrl: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("isMain")
    val isMain: Boolean = false,

    @SerializedName("createdAt")
    val createdAt: String? = null
)