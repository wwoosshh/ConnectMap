package com.example.restaurantapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {
    private const val TAG = "NetworkUtils"

    private const val LOCAL_IP = "58.233.102.165"  // 내 PC의 공용 IP
    private const val PUBLIC_IP = "58.233.102.165" // 외부 접속용 IP

    /**
     * 현재 네트워크 환경에 맞는 서버 URL 반환
     */
    suspend fun getServerUrl(context: Context): String {
        return withContext(Dispatchers.IO) {
            // 에뮬레이터인지 확인
            val isEmulator = isRunningOnEmulator()
            Log.d(TAG, "Is emulator: $isEmulator")

            // 현재 네트워크 정보 확인
            val networkInfo = getNetworkInfo(context)
            Log.d(TAG, "Network info: $networkInfo")

            // 로컬 서버 테스트 (에뮬레이터용)
            if (isEmulator) {
                val emulatorUrl = "http://10.0.2.2:5159/api/"
                if (testConnection(emulatorUrl)) {
                    Log.d(TAG, "Using emulator local URL: $emulatorUrl")
                    return@withContext emulatorUrl
                }
            }

            // 로컬 서버 테스트 (실제 기기용)
            if (isLocalNetwork(context)) {
                val localUrl = "http://127.0.0.1:5159/api/"
                if (testConnection(localUrl)) {
                    Log.d(TAG, "Using local URL: $localUrl")
                    return@withContext localUrl
                }

                // localhost 실패 시 로컬 IP 시도
                val localIpUrl = "http://$LOCAL_IP:5159/api/"
                if (testConnection(localIpUrl)) {
                    Log.d(TAG, "Using local IP URL: $localIpUrl")
                    return@withContext localIpUrl
                }
            }

            // 공용 IP 사용
            val publicUrl = "http://$PUBLIC_IP:5159/api/"
            Log.d(TAG, "Using public URL: $publicUrl")
            return@withContext publicUrl
        }
    }

    /**
     * 에뮬레이터에서 실행 중인지 확인
     */
    private fun isRunningOnEmulator(): Boolean {
        return (android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || "google_sdk" == android.os.Build.PRODUCT)
    }

    /**
     * 로컬 네트워크인지 확인
     */
    private fun isLocalNetwork(context: Context): Boolean {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            // WiFi 연결이고 개발 환경일 가능성이 높음
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check local network: ${e.message}")
            return false
        }
    }

    /**
     * 네트워크 정보 가져오기
     */
    private fun getNetworkInfo(context: Context): String {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return "No network"
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "No capabilities"

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> "Unknown"
            }
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
    }

    /**
     * 서버 연결 테스트
     */
    private suspend fun testConnection(baseUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val testUrl = baseUrl.replace("/api/", "/swagger/index.html")
                val connection = URL(testUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 3000 // 3초
                connection.readTimeout = 3000

                val responseCode = connection.responseCode
                connection.disconnect()

                Log.d(TAG, "Test URL: $testUrl, Response: $responseCode")
                return@withContext responseCode in 200..299 || responseCode == 404
            } catch (e: Exception) {
                Log.d(TAG, "Connection test failed for $baseUrl: ${e.message}")
                return@withContext false
            }
        }
    }
}