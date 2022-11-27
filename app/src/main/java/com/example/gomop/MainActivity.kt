package com.example.gomop

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gomop.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var binding :ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

       // val binding = ActivityMainBinding.inflate(layoutInflater)
        startActivity(Intent(this, SignUpActivity::class.java))
    }

}