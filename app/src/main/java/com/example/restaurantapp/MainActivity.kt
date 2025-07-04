package com.example.restaurantapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.restaurantapp.fragments.HomeFragment
import com.example.restaurantapp.fragments.MapFragment
import com.example.restaurantapp.fragments.MyRestaurantsFragment
import com.example.restaurantapp.fragments.ProfileFragment
import com.example.restaurantapp.utils.TokenManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.restaurantapp.network.ApiClient  // 이 줄 추가

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tokenManager = TokenManager(this)

        // 사용 중인 URL 로그 출력
        android.util.Log.d("MainActivity", "Using server URL: ${ApiClient.getCurrentBaseUrl()}")

        if (!tokenManager.isLoggedIn()) {
            startLoginActivity()
            return
        }

        setupBottomNavigation()

        // 기본 프래그먼트 설정
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_map -> {
                    replaceFragment(MapFragment())
                    true
                }
                R.id.nav_my_restaurants -> {
                    replaceFragment(MyRestaurantsFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun logout() {
        tokenManager.clearAuthData()
        startLoginActivity()
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}