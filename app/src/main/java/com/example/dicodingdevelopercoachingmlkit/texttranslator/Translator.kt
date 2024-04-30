package com.example.dicodingdevelopercoachingmlkit.texttranslator

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

/**
 * Created by Jonathan Darwin on 30 April 2024
 */
class Translator {

    private var translationProgressCount = 0

    val areTranslationAllComplete: Boolean
        get() = translationProgressCount == 0

    fun translate(
        text: String,
        sourceLanguage: Language,
        targetLanguage: Language,
        onSuccess: (text: String) -> Unit,
        onFailure: (e: Exception) -> Unit,
    ) {
        translationProgressCount++

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
                        translationProgressCount--
                        onSuccess(it)
                    }
                    .addOnFailureListener {
                        translationProgressCount--
                        onFailure(it)
                    }
            }
            .addOnFailureListener {
                translationProgressCount--
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