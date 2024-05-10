package com.example.dicodingdevelopercoachingmlkit.texttranslator

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

/**
 * Created by Jonathan Darwin on 30 April 2024
 */
class TextTranslator {

    private val translatorMap = mutableMapOf<String, Translator>()

    private var translationProgressCount = 0

    private val downloadConditions = DownloadConditions.Builder()
                                        .requireWifi()
                                        .build()

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

        val translator = getTranslator(sourceLanguage, targetLanguage)

        translator
            .downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                translator.translate(text)
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

    fun close() {
        translatorMap.values.forEach {
            it.close()
        }
    }

    private fun getTranslator(
        sourceLanguage: Language,
        targetLanguage: Language,
    ): Translator {
        val translatorKey = "%s#%s".format(sourceLanguage.key, targetLanguage.key)
        val translator = translatorMap[translatorKey]

        return translator ?: run {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(getTranslateLanguage(sourceLanguage))
                .setTargetLanguage(getTranslateLanguage(targetLanguage))
                .build()

            Translation.getClient(options).also {
                translatorMap[translatorKey] = it
            }
        }
    }

    private fun getTranslateLanguage(language: Language): String {
        return when (language) {
            Language.English -> TranslateLanguage.ENGLISH
            Language.Bahasa -> TranslateLanguage.INDONESIAN
        }
    }
}