package com.example.linguaforge.activitys

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.linguaforge.R
import com.example.linguaforge.models.utils.Utils
import com.example.linguaforge.models.utils.UtilsDB
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import android.media.MediaPlayer
import android.os.Vibrator
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

class Modo1Activity : AppCompatActivity() {
    private final var MAXIMO_VIDAS: Int = 3
    private lateinit var clave: String
    private var palabras: List<List<String>>? = null
    private var palabra: TextView? = null
    private var pregunta: TextView? = null
    private var respuesta1: TextView? = null
    private var respuesta2: TextView? = null
    private var respuesta3: TextView? = null
    private var respuesta4: TextView? = null
    private var marcador: TextView? = null
    private var marcadorVidas: TextView? = null
    private var corazonImageView: ImageView? = null
    private var respuestaCorrecta: String? = null // Variable para almacenar la respuesta correcta
    private var aciertos: Int? = 0 // Variable para almacenar la respuesta correcta
    private var jugadas: Int? = 0 // Variable para almacenar la respuesta correcta


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modo1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        clave = intent.getStringExtra("clave") ?: ""
        obtenerPalabras()

        palabra = findViewById(R.id.palabra)
        pregunta = findViewById(R.id.pregunta)
        respuesta1 = findViewById(R.id.respuesta1)
        respuesta2 = findViewById(R.id.respuesta2)
        respuesta3 = findViewById(R.id.respuesta3)
        respuesta4 = findViewById(R.id.respuesta4)
        marcador = findViewById(R.id.marcadorTextView)
        marcadorVidas = findViewById(R.id.marcadorVidasTextView)
        corazonImageView = findViewById(R.id.corazonImageView)
        cargarPalabras()

        configurarListenersRespuestas(respuesta1!!)
        configurarListenersRespuestas(respuesta2!!)
        configurarListenersRespuestas(respuesta3!!)
        configurarListenersRespuestas(respuesta4!!)

    }

    @SuppressLint("ClickableViewAccessibility")
    fun configurarListenersRespuestas(textView: TextView) {
        textView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Escalar a 1.1 veces el tamaño original en ambos ejes
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Volver al tamaño original
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()

                    // Comprobar la jugada solo si se levanta el dedo del elemento
                    if (event.action == MotionEvent.ACTION_UP) {
                        comprobarJugada(textView)
                    }
                }
            }
            true
        }
    }


    private fun actualizarMarcador() {
        val totalJugadas = palabras?.size ?: 9999
        val totalAciertos = aciertos ?: 0
        val totalJugadasRealizadas = jugadas ?: 0
        // Los fallos son el total de jugadas menos los aciertos.
        val totalFallos = totalJugadasRealizadas - totalAciertos
        if (totalFallos > MAXIMO_VIDAS) {
            finalizar()
        }
        marcador?.text = "$totalJugadasRealizadas / $totalJugadas"
        // Las vidas se calculan como 3 menos los fallos.
        marcadorVidas?.text = "X${MAXIMO_VIDAS - totalFallos}"

    }


    private fun obtenerPalabras() = runBlocking {
        palabras = Utils.obtenerPalabrasPorClave(clave)?.shuffled()

    }

    private fun cargarPalabras() {
        // Asegurarse de que hay palabras y el índice no supera el tamaño de la lista
        if (palabras != null && palabras!!.isNotEmpty() && jugadas!! < palabras!!.size) {
            val grupoActual = palabras!![jugadas!!]

            val palabraSeleccionada = grupoActual.getOrNull(0) ?: ""
            val traduccionSeleccionada = grupoActual.getOrNull(1) ?: ""
            respuestaCorrecta = palabraSeleccionada

            palabra?.text = traduccionSeleccionada
            pregunta?.text = "¿Cuál de estas es $traduccionSeleccionada?"

            // Crear una lista con la palabra seleccionada y añadir tres palabras aleatorias
            val opciones = mutableListOf(palabraSeleccionada)
            while (opciones.size < 4) {
                val indiceAleatorio = Random.nextInt(palabras!!.size)
                val palabraAleatoria = palabras!![indiceAleatorio].first()
                // Asegurar que no se añada una palabra repetida
                if (!opciones.contains(palabraAleatoria)) {
                    opciones.add(palabraAleatoria)
                }
            }

            // Mezclar las opciones y asignarlas a los TextViews de respuesta
            opciones.shuffle()
            respuesta1?.text = opciones[0]
            respuesta2?.text = opciones[1]
            respuesta3?.text = opciones[2]
            respuesta4?.text = opciones[3]

            resetearEstilo(respuesta1)
            resetearEstilo(respuesta2)
            resetearEstilo(respuesta3)
            resetearEstilo(respuesta4)


        } else {
            finalizar()
        }
    }

    private fun finalizar() {
        finish()
    }

    private fun resetearEstilo(textView: TextView?) {
        // Obtener el Drawable del archivo XML y establecerlo como background
        textView?.background = ContextCompat.getDrawable(this, R.drawable.style_textview)

        // Establecer el color del texto a negro
        textView?.setTextColor(Color.BLACK)
    }


    fun comprobarJugada(textViewElegida: TextView) {
        val acierto = textViewElegida.text.equals(respuestaCorrecta)
        if (acierto) {
            aciertos = aciertos!! + 1
            colorRespuesta(textViewElegida, true)
            val mediaPlayer = MediaPlayer.create(this, R.raw.correct_sound)
            mediaPlayer.start()
        } else {
            colorRespuesta(textViewElegida, false)
            animacionPerdidaVida()

            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(500)
            // Vibrar por 500 milisegundos


            val mediaPlayer = MediaPlayer.create(
                this,
                R.raw.error_sound
            ) // Suponiendo que tienes un archivo error_sound.mp3 en res/raw
            mediaPlayer.start()
        }

        // Incrementar jugadas aquí garantiza que se cuenta cada intento correctamente.
        jugadas = jugadas!! + 1

        // Actualizar el marcador después de cada intento.
        actualizarMarcador()

        // Cargar nuevas palabras después de un retraso.
        Handler(Looper.getMainLooper()).postDelayed({
            cargarPalabras()
        }, 1000)
    }


    private fun colorRespuesta(textView: TextView, esCorrecto: Boolean) {
        // Establecer el color del texto a blanco
        textView.setTextColor(Color.WHITE)

        // Obtener el Drawable correspondiente y establecerlo como background
        val drawableId = if (esCorrecto) R.drawable.style_acierto_textview else R.drawable.style_error_textview
        val background = ContextCompat.getDrawable(this, drawableId)
        textView.background = background
    }


    fun irAtras(view: View) {
        onBackPressed()
    }

    fun animacionPerdidaVida() {
        val corazonImageView = findViewById<ImageView>(R.id.corazonImageView)

        // Define los colores para la animación.
        val colorOriginal = ContextCompat.getColor(this, R.color.azulOscuro)
        val colorRojo = ContextCompat.getColor(this, android.R.color.holo_red_dark)

        // Crea y configura la animación de cambio de color.
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorRojo, colorOriginal)
        colorAnimation.duration = 1000 // Duración en milisegundos
        colorAnimation.addUpdateListener { animator ->
            corazonImageView.setColorFilter(animator.animatedValue as Int, PorterDuff.Mode.SRC_IN)
        }

        // Inicia la animación de cambio de color.
        colorAnimation.start()

        // Inicia la animación de pulso con YoYo.
        YoYo.with(Techniques.Swing)
            .duration(100).repeat(5)
            .playOn(corazonImageView)
        YoYo.with(Techniques.Swing)
            .duration(300).delay(500)
            .playOn(corazonImageView)
    }



}
