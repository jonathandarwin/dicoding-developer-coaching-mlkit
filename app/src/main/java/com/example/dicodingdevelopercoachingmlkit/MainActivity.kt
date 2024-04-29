package com.example.dicodingdevelopercoachingmlkit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dicodingdevelopercoachingmlkit.barcodescanner.BarcodeScannerActivity
import com.example.dicodingdevelopercoachingmlkit.databinding.ActivityMainBinding
import com.example.dicodingdevelopercoachingmlkit.texttranslator.TextTranslatorActivity
import com.example.dicodingdevelopercoachingmlkit.yawndetector.YawnDetectorActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnYawnDetector.setOnClickListener {
            startActivity(Intent(this, YawnDetectorActivity::class.java))
        }

        binding.btnTextTranslator.setOnClickListener {
            startActivity(Intent(this, TextTranslatorActivity::class.java))
        }

        binding.btnBarcodeScanner.setOnClickListener {
            startActivity(Intent(this, BarcodeScannerActivity::class.java))
        }
    }
}