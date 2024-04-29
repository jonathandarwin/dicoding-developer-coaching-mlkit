package com.example.dicodingdevelopercoachingmlkit.texttranslator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingdevelopercoachingmlkit.databinding.ActivityTextTranslatorBinding

/**
 * Created by Jonathan Darwin on 29 April 2024
 */
class TextTranslatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextTranslatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}