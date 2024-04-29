package com.example.dicodingdevelopercoachingmlkit.yawndetector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingdevelopercoachingmlkit.databinding.ActivityYawnDetectorBinding

/**
 * Created by Jonathan Darwin on 29 April 2024
 */
class YawnDetectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYawnDetectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYawnDetectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}