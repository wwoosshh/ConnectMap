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