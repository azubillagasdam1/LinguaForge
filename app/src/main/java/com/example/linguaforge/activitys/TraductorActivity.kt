package com.example.linguaforge.activitys

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ImageView
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
import java.util.Random


class TraductorActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION_CODE = 1
    private var fromLanguageCode = 0
    private var toLanguageCode = 0

    private var idioma1 = ""
    private var idioma2 = ""
    private var clave = ""
    private var source = ""
    private var translatedText = ""


    private var idioma1TextView: TextView? = null
    private var idioma2TextView: TextView? = null
    private var sourceText: TextInputEditText? = null
    private var micIV: ImageView? = null
    private var translateBtn: MaterialButton? = null
    private var translateTV: TextView? = null
    private var papelera: ImageView? = null
    private var check: ImageView? = null
    private lateinit var fondos: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traductor)
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
        check = findViewById(R.id.checkImageView)

        setupBackgroundImages()
        animateBackgrounds()
        setIdiomas()





        micIV?.setOnClickListener {
            val mediaPlayer = MediaPlayer.create(this, R.raw.click_go_sound)
            mediaPlayer.start()
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something to translate")

            try {
                startActivityForResult(intent, REQUEST_PERMISSION_CODE)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@TraductorActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        translateBtn?.setOnClickListener {
            var papelera = findViewById<ImageView>(R.id.idPapeleraImageView)
            translateTV?.visibility = View.VISIBLE
            papelera?.visibility = View.VISIBLE
            translateTV?.text = ""

            if (sourceText?.text.toString().isEmpty()) {
                Toast.makeText(
                    this@TraductorActivity,
                    "Please enter text to translate",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (fromLanguageCode == 0) {
                Toast.makeText(
                    this@TraductorActivity,
                    "Please select Source Language",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (toLanguageCode == 0) {
                Toast.makeText(
                    this@TraductorActivity,
                    "Please select the language to make translation",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                translateText(fromLanguageCode, toLanguageCode, sourceText?.text.toString())
            }
        }
        sourceText?.setOnClickListener {

            // Verifica si la traducción ya existe
            var existeTraduccion = Utils.existeTraduccion(source, translatedText)
// Verifica si las variables son vacías
            var palabra1vacia = sourceText?.text.isNullOrEmpty()
            var palabra2vacia = translatedText.isNullOrEmpty()

            if (!palabra1vacia && !palabra2vacia) {
                if (!existeTraduccion) {
                    // Si no está vacío y no existe, guarda la palabra
                    guardarPalabra(clave, source, translatedText)
                } else {
                    // Si la traducción ya existe, muestra el mensaje "ya existe"
                    val mensajeOriginal = translateTV?.text.toString()
                    translateTV?.text = "ya existe"

                    // Crea un Handler para revertir el mensaje después de 1 segundo
                    Handler(Looper.getMainLooper()).postDelayed({
                        translateTV?.text = mensajeOriginal
                    }, 1000)
                }
            }

            sourceText?.setText("", TextView.BufferType.EDITABLE)
        }


        papelera!!.setOnClickListener {

            // Llama a tu función para eliminar la palabra aquí
            Utils.eliminarPalabra(clave, 999999)
            papelera?.visibility = View.GONE
            translateTV?.visibility = View.GONE

        }

    }
    private fun setupBackgroundImages() {
        fondos = listOf(
            findViewById(R.id.fondoletras1),
            findViewById(R.id.fondoletras2),
            findViewById(R.id.fondoletras3),
            findViewById(R.id.fondoletras4)
        )
    }
    private fun animateBackgrounds() {
        val random = Random()
        fondos.forEachIndexed { index, imageView ->
            imageView.alpha = 0f
            animateBackground(imageView, random, index * 1000L)
        }
    }
    private fun animateBackground(view: ImageView, random: Random, delay: Long) {
        val fadeInDuration = 3000L + random.nextInt(1000)
        val fadeOutDuration = 3000L + random.nextInt(1000)

        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = delay
        }
        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
            duration = fadeOutDuration
            startDelay = fadeInDuration
        }
        val set = AnimatorSet().apply {
            playSequentially(fadeIn, fadeOut)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    animateBackground(view, random, 0) // Loop the animation
                }
            })
        }
        set.start()
    }


    private fun setIdiomas() {
        fromLanguageCode = Utils.getLanguageFirebaseCode(Utils.getLanguageByCountryCode(idioma1))
        toLanguageCode = Utils.getLanguageFirebaseCode(Utils.getLanguageByCountryCode(idioma2))
        idioma1TextView?.text =
            Utils.getFlagEmoji(Utils.getCountryCode(Utils.getLanguageByCountryCode(idioma1)))
        idioma2TextView?.text =
            Utils.getFlagEmoji(Utils.getCountryCode(Utils.getLanguageByCountryCode(idioma2)))
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

                this.source = source
                this.translatedText = translatedText
                papelera?.visibility = View.VISIBLE


            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@TraductorActivity,
                    "Failed to translate! Try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                this@TraductorActivity,
                "Failed to download model!! Check your internet connection.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun guardarPalabra(clave: String, source: String, translatedText: String) {
        Utils.anadirPalabra(clave, source, translatedText)
        efectoCheck()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun efectoCheck() {
        // Fade in (0 to 1 alpha) animation
        val fadeIn = ObjectAnimator.ofFloat(check, "alpha", 0f, 1f).apply {
            duration = 50
        }

        // Fade out (1 to 0 alpha) animation
        val fadeOut = ObjectAnimator.ofFloat(check, "alpha", 1f, 0f).apply {
            duration = 1000
            startDelay = 50 // Start fade out after fade in is complete
        }

        // Play the animations sequentially
        val set = AnimatorSet().apply {
            playSequentially(fadeIn, fadeOut)
        }

        set.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val editable: Editable = SpannableStringBuilder(result?.get(0))
            sourceText?.text = editable
        }
    }


    fun irAtras(view: View) {
        val mediaPlayer = MediaPlayer.create(this, R.raw.click_back_sound)
        mediaPlayer.start()
        var palabra1vacia = sourceText?.text.isNullOrEmpty()
        var palabra2vacia = translatedText.isNullOrEmpty()
        var existeTraduccion = Utils.existeTraduccion(source, translatedText)

        if (!palabra1vacia && !palabra2vacia) {
            if (!existeTraduccion) {


                guardarPalabra(clave, source, translatedText)
            }
        }
        onBackPressed()
    }

}
