package com.example.gomop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.gomop.databinding.ActivityMainBinding
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {
    private lateinit var binding :ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        startActivity(Intent(this, SignUpActivity::class.java))

/*
        var storage: FirebaseStorage?
        storage = FirebaseStorage.getInstance()
        var storageRef = storage?.reference?.child("images")?.child("imageFileName")
        Log.d("storage? ", storage.toString())
        Log.d("storage?2 ", storage.reference?.toString())
        Log.d("storage?3 ", storage.reference.child("images")?.toString())
        Log.d("storage?4 ", storage.reference.child("uid")?.child("TarcIGTAwQMWBZijBiill1GUcm83").child("images").toString())
*/
    }
}