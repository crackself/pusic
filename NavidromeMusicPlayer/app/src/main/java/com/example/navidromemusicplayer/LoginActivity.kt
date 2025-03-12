package com.example.navidromemusicplayer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {
            val serverUrl = etServerUrl.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (serverUrl.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 模拟登录过程，实际项目中应调用 API 验证
            val sp = getSharedPreferences("config", MODE_PRIVATE)
            sp.edit().apply {
                putString("server_url", serverUrl)
                putString("username", username)
                putString("password", password)
                apply()
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
