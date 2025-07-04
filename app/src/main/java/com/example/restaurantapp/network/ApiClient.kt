package com.example.restaurantapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // 동적 URL 결정 로직
    private fun getServerUrl(): String {
        // 에뮬레이터 감지 로직들
        val fingerprint = android.os.Build.FINGERPRINT
        val model = android.os.Build.MODEL
        val brand = android.os.Build.BRAND
        val device = android.os.Build.DEVICE
        val product = android.os.Build.PRODUCT
        val manufacturer = android.os.Build.MANUFACTURER

        // 디버깅용 로그
        android.util.Log.d("ApiClient", "FINGERPRINT: $fingerprint")
        android.util.Log.d("ApiClient", "MODEL: $model")
        android.util.Log.d("ApiClient", "BRAND: $brand")
        android.util.Log.d("ApiClient", "DEVICE: $device")
        android.util.Log.d("ApiClient", "PRODUCT: $product")
        android.util.Log.d("ApiClient", "MANUFACTURER: $manufacturer")

        val isEmulator = fingerprint.startsWith("generic") ||
                fingerprint.startsWith("unknown") ||
                model.contains("google_sdk") ||
                model.contains("Emulator") ||
                model.contains("Android SDK built for x86") ||
                model.contains("Android SDK built for x86_64") ||
                manufacturer.contains("Genymotion") ||
                (brand.startsWith("generic") && device.startsWith("generic")) ||
                product == "google_sdk" ||
                product.contains("sdk") ||
                product.contains("emulator")

        android.util.Log.d("ApiClient", "Is emulator detected: $isEmulator")

        val url = if (isEmulator) {
            "http://10.0.2.2:5159/api/"
        } else {
            "http://58.233.102.165:5159/api/"
        }

        android.util.Log.d("ApiClient", "Selected URL: $url")
        return url
    }

    private val BASE_URL = getServerUrl()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    fun getCurrentBaseUrl(): String = BASE_URL
}