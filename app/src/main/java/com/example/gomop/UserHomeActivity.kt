package com.example.gomop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.example.gomop.databinding.ActivityUserHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.Calendar.getInstance

class UserHomeActivity : AppCompatActivity() {
    private lateinit var binding :ActivityUserHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val email = user!!.email!!
        val uid = user!!.uid
        Log.d("eeeeeeeeeeeeeeeeeeeeeeeemail",email)
        Log.d("Ueeeeeeeeeeeeeeeeeeeeeeeeeeeeeid",uid)

        binding.userinfo.text = "Email: ${email}\nUid: ${uid}"

    }

}