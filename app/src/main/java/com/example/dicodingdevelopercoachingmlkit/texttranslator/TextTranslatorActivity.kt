package com.example.dicodingdevelopercoachingmlkit.texttranslator

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingdevelopercoachingmlkit.databinding.ActivityTextTranslatorBinding

/**
 * Created by Jonathan Darwin on 29 April 2024
 */
class TextTranslatorActivity : AppCompatActivity() {

    private var language = Language.English

    private val textTranslator = TextTranslator()

    private lateinit var binding: ActivityTextTranslatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTranslate.setOnClickListener {
            showTranslateLoader()

            translateAllText()

            language = language.switch()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textTranslator.close()
    }

    private fun translateAllText() {
        traverseAllViewChild(binding.root) {
            translateText(it)
        }
    }

    private fun translateText(textView: TextView) {
        textTranslator.translate(
            text = textView.text.toString(),
            sourceLanguage = language,
            targetLanguage = language.switch(),
            onSuccess = {
                textView.text = it
                checkLoader()
            },
            onFailure = {
                Toast.makeText(this, "Translation error : $it", Toast.LENGTH_SHORT).show()
                checkLoader()
            }
        )
    }

    private fun traverseAllViewChild(viewGroup: ViewGroup, onTextViewFound: (TextView) -> Unit) {
        for (i in 0 until viewGroup.childCount) {
            when(val view = viewGroup.getChildAt(i)) {
                is ViewGroup -> traverseAllViewChild(view, onTextViewFound)
                is TextView -> onTextViewFound(view)
            }
        }
    }

    private fun checkLoader() {
        if (textTranslator.areTranslationAllComplete) {
            hideTranslateLoader()
        } else {
            showTranslateLoader()
        }
    }

    private fun showTranslateLoader() {
        binding.loader.visibility = View.VISIBLE
        binding.tvTranslate.visibility = View.GONE
    }

    private fun hideTranslateLoader() {
        binding.loader.visibility = View.GONE
        binding.tvTranslate.visibility = View.VISIBLE
    }
}