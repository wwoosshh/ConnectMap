package com.example.restaurantapp.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("username")
    val username: String = "",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("passwordHash")
    val passwordHash: String = "",

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("profile")
    val profile: UserProfile? = null
)

data class UserProfile(
    @SerializedName("profileId")
    val profileId: Int = 0,

    @SerializedName("userId")
    val userId: Int = 0,

    @SerializedName("fullName")
    val fullName: String = "",

    @SerializedName("phone")
    val phone: String = "",

    @SerializedName("birthDate")
    val birthDate: String? = null,

    @SerializedName("gender")
    val gender: String = "",

    @SerializedName("address")
    val address: String = "",

    @SerializedName("profileImageUrl")
    val profileImageUrl: String = "",

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class UserRestaurantPreference(
    @SerializedName("preferenceId")
    val preferenceId: Int = 0,

    @SerializedName("userId")
    val userId: Int = 0,

    @SerializedName("restaurantId")
    val restaurantId: Int = 0,

    @SerializedName("preferenceType")
    val preferenceType: String = "", // saved, liked, disliked

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("restaurant")
    val restaurant: Restaurant? = null
)

// 요청 모델들
data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String? = null,
    val phone: String? = null
)

data class UpdateProfileRequest(
    val fullName: String? = null,
    val phone: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val address: String? = null,
    val profileImageUrl: String? = null
)

data class LoginResponse(
    val token: String,
    val user: User
)

// 선호도 타입 상수들
object PreferenceType {
    const val SAVED = "saved"
    const val LIKED = "liked"
    const val DISLIKED = "disliked"
}