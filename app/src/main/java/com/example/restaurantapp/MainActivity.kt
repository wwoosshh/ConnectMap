package com.example.restaurantapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.models.Restaurant
import com.example.restaurantapp.network.ApiClient
import com.example.restaurantapp.utils.TokenManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tokenManager = TokenManager(this)

        if (!tokenManager.isLoggedIn()) {
            startLoginActivity()
            return
        }

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadRestaurants()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewRestaurants)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
    }

    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter { restaurant ->
            // 맛집 클릭 시 상세 정보 표시 (나중에 구현)
            Toast.makeText(this, "${restaurant.name} 클릭됨", Toast.LENGTH_SHORT).show()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = restaurantAdapter
        }
    }

    private fun setupClickListeners() {
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnRefresh = findViewById<Button>(R.id.btnRefresh)

        btnLogout.setOnClickListener {
            tokenManager.clearAuthData()
            startLoginActivity()
        }

        btnRefresh.setOnClickListener {
            loadRestaurants()
        }
    }

    private fun loadRestaurants() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getRestaurants()

                // 디버깅용 로그
                android.util.Log.d("MainActivity", "Response code: ${response.code()}")
                android.util.Log.d("MainActivity", "Response body: ${response.body()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val restaurants = response.body()?.data ?: emptyList()

                    // 디버깅용 로그
                    android.util.Log.d("MainActivity", "Restaurants count: ${restaurants.size}")
                    restaurants.forEach { restaurant ->
                        android.util.Log.d("MainActivity", "Restaurant: ${restaurant.name}")
                    }

                    displayRestaurants(restaurants)

                    Toast.makeText(
                        this@MainActivity,
                        "${restaurants.size}개의 맛집을 불러왔습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val errorMessage = response.body()?.error ?: "맛집 목록을 불러오지 못했습니다"
                    android.util.Log.e("MainActivity", "Error: $errorMessage")
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                    displayRestaurants(emptyList())
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Exception: ${e.message}", e)
                Toast.makeText(
                    this@MainActivity,
                    "네트워크 오류: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                displayRestaurants(emptyList())
            } finally {
                showLoading(false)
            }
        }
    }

    private fun displayRestaurants(restaurants: List<Restaurant>) {
        if (restaurants.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvEmptyMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvEmptyMessage.visibility = View.GONE
            restaurantAdapter.updateRestaurants(restaurants)
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}