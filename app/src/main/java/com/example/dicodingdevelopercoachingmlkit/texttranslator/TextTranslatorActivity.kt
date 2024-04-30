package com.example.dicodingdevelopercoachingmlkit.texttranslator

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingdevelopercoachingmlkit.databinding.ActivityTextTranslatorBinding

/**
 * Created by Jonathan Darwin on 29 April 2024
 */
class TextTranslatorActivity : AppCompatActivity() {

    private var language = Language.English

    private val translator = Translator()

    private lateinit var binding: ActivityTextTranslatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTranslate.setOnClickListener {
            translateAllText()

            language = language.switch()
        }
    }

    private fun translateAllText() {
        traverseAllViewChild(binding.root) {
            translateText(it)
        }
    }

    private fun translateText(textView: TextView) {
        translator.translate(
            text = textView.text.toString(),
            sourceLanguage = language,
            targetLanguage = language.switch(),
            onSuccess = {
                textView.text = it
            },
            onFailure = {

            }
        )
    }

    private fun traverseAllViewChild(viewGroup: ViewGroup, onTextViewFound: (TextView) -> Unit) {
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)

            when(view) {
                is ViewGroup -> traverseAllViewChild(view, onTextViewFound)
                is TextView -> onTextViewFound(view)
            }
        }
    }
}