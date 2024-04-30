package com.example.dicodingdevelopercoachingmlkit.texttranslator

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

/**
 * Created by Jonathan Darwin on 30 April 2024
 */
class Translator {

    fun translate(
        text: String,
        sourceLanguage: Language,
        targetLanguage: Language,
        onSuccess: (text: String) -> Unit,
        onFailure: (e: Exception) -> Unit,
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(getTranslateLanguage(sourceLanguage))
            .setTargetLanguage(getTranslateLanguage(targetLanguage))
            .build()

        val client = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        client
            .downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                client.translate(text)
                    .addOnSuccessListener {
                        onSuccess(it)
                    }
                    .addOnFailureListener {
                        onFailure(it)
                    }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    private fun getTranslateLanguage(language: Language): String {
        return when (language) {
            Language.English -> TranslateLanguage.ENGLISH
            Language.Bahasa -> TranslateLanguage.INDONESIAN
        }
    }
}