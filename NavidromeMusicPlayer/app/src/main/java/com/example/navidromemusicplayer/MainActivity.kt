package com.pusic

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pusic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化 SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // 检查用户是否已登录
        checkUserLogin()

        // 设置 RecyclerView
        setupRecyclerView()

        // 设置按钮监听
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    /**
     * 检查用户是否已登录
     */
    private fun checkUserLogin() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            binding.etUsername.setText(sharedPreferences.getString("username", ""))
            binding.etPassword.setText(sharedPreferences.getString("password", ""))
            Toast.makeText(this, "已自动登录", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 执行登录逻辑
     */
    private fun performLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        // 模拟登录验证（可以替换为 API 请求）
        if (username == "admin" && password == "123456") {
            // 保存登录状态
            with(sharedPreferences.edit()) {
                putBoolean("isLoggedIn", true)
                putString("username", username)
                putString("password", password)
                apply()
            }

            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
            navigateToHome()
        } else {
            Toast.makeText(this, "登录失败，请检查用户名或密码", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 退出登录
     */
    private fun logout() {
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            putString("username", "")
            putString("password", "")
            apply()
        }
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show()
    }

    /**
     * 跳转到首页
     */
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * 设置 RecyclerView（示例）
     */
    private fun setupRecyclerView() {
        binding.recyclerViewTracks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTracks.adapter = TrackAdapter(getDummyTracks())
    }

    /**
     * 模拟音乐数据
     */
    private fun getDummyTracks(): List<String> {
        return listOf("Song 1", "Song 2", "Song 3", "Song 4", "Song 5")
    }
}
