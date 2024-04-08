package com.example.linguaforge.activitys

import android.app.AlertDialog
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.linguaforge.R
import com.example.linguaforge.models.utils.Utils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION_CODE = 1
    private var fromLanguageCode = 0
    private var toLanguageCode = 0

    private var idioma1 = ""
    private var idioma2 = ""
    private var clave = ""

    private var idioma1TextView: TextView? = null
    private var idioma2TextView: TextView? = null
    private var sourceText: TextInputEditText? = null
    private var micIV: ImageView? = null
    private var translateBtn: MaterialButton? = null
    private var translateTV: TextView? = null
    private var papelera: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        idioma1 = intent.getStringExtra("idioma1") ?: ""
        idioma2 = intent.getStringExtra("idioma2") ?: ""
        clave = intent.getStringExtra("clave") ?: ""

        idioma1TextView = findViewById(R.id.idioma1TextView)
        idioma2TextView = findViewById(R.id.idioma2TextView)
        sourceText = findViewById(R.id.idEditSource)
        micIV = findViewById(R.id.idIVMic)
        translateBtn = findViewById(R.id.idBtnTranslation)
        translateTV = findViewById(R.id.idTranslatedTV)
        papelera = findViewById(R.id.idPapeleraImageView)


        setIdiomas()





        micIV?.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something to translate")

            try {
                startActivityForResult(intent, REQUEST_PERMISSION_CODE)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        translateBtn?.setOnClickListener {
            var papelera = findViewById<ImageView>(R.id.idPapeleraImageView)
            translateTV?.visibility = View.VISIBLE
            papelera?.visibility = View.VISIBLE
            translateTV?.text = ""

            if (sourceText?.text.toString().isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter text to translate", Toast.LENGTH_SHORT).show()
            } else if (fromLanguageCode == 0) {
                Toast.makeText(this@MainActivity, "Please select Source Language", Toast.LENGTH_SHORT).show()
            } else if (toLanguageCode == 0) {
                Toast.makeText(this@MainActivity, "Please select the language to make translation", Toast.LENGTH_SHORT).show()
            } else {
                translateText(fromLanguageCode, toLanguageCode, sourceText?.text.toString())
            }
        }
        sourceText?.setOnClickListener {
            sourceText?.setText("", TextView.BufferType.EDITABLE)
        }


        papelera!!.setOnClickListener {

            // Llama a tu función para eliminar la palabra aquí
            Utils.eliminarPalabra(clave, 999999)
            papelera?.visibility = View.GONE
            translateTV?.visibility = View.GONE

        }

    }

    private fun setIdiomas() {
        fromLanguageCode = Utils.getLanguageFirebaseCode(Utils.getLanguageByCountryCode(idioma1))
        toLanguageCode = Utils.getLanguageFirebaseCode(Utils.getLanguageByCountryCode(idioma2))
        idioma1TextView?.text = Utils.getFlagEmoji(Utils.getCountryCode(Utils.getLanguageByCountryCode(idioma1)))
        idioma2TextView?.text = Utils.getFlagEmoji(Utils.getCountryCode(Utils.getLanguageByCountryCode(idioma2)))
    }

    private fun translateText(fromLanguageCode: Int, toLanguageCode: Int, source: String) {
        translateTV?.text = "Downloading model! Please wait..."

        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(fromLanguageCode)
            .setTargetLanguage(toLanguageCode)
            .build()

        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

        val conditions = FirebaseModelDownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
            translateTV?.text = ""
            translator.translate(source).addOnSuccessListener { translatedText ->
                translateTV?.text = translatedText

                papelera?.visibility = View.VISIBLE
                Utils.anadirPalabra(clave,source,translatedText)

            }.addOnFailureListener { e ->
                Toast.makeText(this@MainActivity, "Failed to translate! Try again", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this@MainActivity, "Failed to download model!! Check your internet connection.", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val editable: Editable = SpannableStringBuilder(result?.get(0))
            sourceText?.text = editable
        }
    }


    fun irRecicler(view: View) {
        val intent = Intent(this, IdiomaActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun irLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}
