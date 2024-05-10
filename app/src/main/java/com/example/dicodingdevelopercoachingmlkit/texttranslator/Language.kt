package com.example.dicodingdevelopercoachingmlkit.texttranslator

/**
 * Created by Jonathan Darwin on 30 April 2024
 */
enum class Language(val key: String) {
    Bahasa("bahasa"),
    English("english");

    fun switch(): Language {
        return if (this == Bahasa) English else Bahasa
    }
}