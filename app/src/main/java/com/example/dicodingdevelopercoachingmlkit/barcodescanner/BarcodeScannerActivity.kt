package com.example.dicodingdevelopercoachingmlkit.barcodescanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingdevelopercoachingmlkit.databinding.ActivityBarcodeScannerBinding

/**
 * Created by Jonathan Darwin on 29 April 2024
 */
class BarcodeScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}