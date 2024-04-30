package com.example.dicodingdevelopercoachingmlkit.texttranslator

/**
 * Created by Jonathan Darwin on 30 April 2024
 */
enum class Language {
    Bahasa,
    English;

    fun switch(): Language {
        return if (this == Bahasa) English else Bahasa
    }
}