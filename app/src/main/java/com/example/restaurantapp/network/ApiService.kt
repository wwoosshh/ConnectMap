package com.example.restaurantapp.network

import com.example.restaurantapp.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 인증 관련
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<LoginResponse>>

    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<ApiResponse<User>>

    // 맛집 관련
    @GET("restaurants")
    suspend fun getRestaurants(): Response<ApiResponse<List<Restaurant>>>

    // 필터링된 맛집 조회
    @GET("restaurants/filter")
    suspend fun getFilteredRestaurants(
        @Query("cuisineTypeId") cuisineTypeId: Int? = null,
        @Query("location") location: String? = null,
        @Query("minRating") minRating: Float? = null,
        @Query("maxRating") maxRating: Float? = null,
        @Query("minReviews") minReviews: Int? = null,
        @Query("sortBy") sortBy: String? = "name",
        @Query("sortOrder") sortOrder: String? = "asc"
    ): Response<ApiResponse<List<Restaurant>>>

    // 요리 종류별 맛집 조회
    @GET("restaurants/cuisine/{cuisineTypeId}")
    suspend fun getRestaurantsByCuisine(@Path("cuisineTypeId") cuisineTypeId: Int): Response<ApiResponse<List<Restaurant>>>

    // 요리 종류 목록 조회
    @GET("restaurants/cuisine-types")
    suspend fun getCuisineTypes(): Response<ApiResponse<List<CuisineType>>>

    @GET("restaurants/{id}")
    suspend fun getRestaurant(@Path("id") id: Int): Response<ApiResponse<Restaurant>>

    @POST("restaurants")
    suspend fun createRestaurant(
        @Header("Authorization") token: String,
        @Body restaurant: Restaurant
    ): Response<ApiResponse<Restaurant>>

    @PUT("restaurants/{id}")
    suspend fun updateRestaurant(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body restaurant: Restaurant
    ): Response<ApiResponse<Restaurant>>

    @DELETE("restaurants/{id}")
    suspend fun deleteRestaurant(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>
}