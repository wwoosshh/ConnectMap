package com.example.restaurantapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.restaurantapp.models.RegisterRequest
import com.example.restaurantapp.network.ApiClient
import com.example.restaurantapp.utils.TokenManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tokenManager = TokenManager(this)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPasswordConfirm = findViewById<EditText>(R.id.etPasswordConfirm)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val passwordConfirm = etPasswordConfirm.text.toString().trim()

            if (validateInput(username, email, password, passwordConfirm)) {
                performRegister(username, email, password)
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(username: String, email: String, password: String, passwordConfirm: String): Boolean {
        if (username.isEmpty()) {
            Toast.makeText(this, "사용자명을 입력하세요", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != passwordConfirm) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 4) {
            Toast.makeText(this, "비밀번호는 4자 이상이어야 합니다", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun performRegister(username: String, email: String, password: String) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val registerRequest = RegisterRequest(username, email, password)
                val response = ApiClient.apiService.register(registerRequest)

                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()?.data
                    if (loginResponse != null) {
                        tokenManager.saveAuthData(loginResponse.token, loginResponse.user)
                        Toast.makeText(this@RegisterActivity, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    val errorMessage = response.body()?.error ?: "회원가입에 실패했습니다"
                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "네트워크 오류: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnBack = findViewById<Button>(R.id.btnBack)

        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !show
        btnBack.isEnabled = !show
    }
}