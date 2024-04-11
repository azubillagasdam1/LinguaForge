package com.example.linguaforge.activitys

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
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
        cargarPalabras()

        // Definir onTouchListeners para los TextViews de respuesta
        // Definir onTouchListeners para los TextViews de respuesta
        respuesta1?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                comprobarJugada(respuesta1!!)
            }
            true // Retorna true para indicar que el evento ha sido manejado
        }

        respuesta2?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                comprobarJugada(respuesta2!!)
            }
            true
        }

        respuesta3?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                comprobarJugada(respuesta3!!)
            }
            true
        }

        respuesta4?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                comprobarJugada(respuesta4!!)
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
        if(totalFallos>MAXIMO_VIDAS){
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


        }else{
            finalizar()
        }
    }

    private fun finalizar() {
        finish()
    }

    private fun resetearEstilo(textView: TextView?) {
        textView?.setBackgroundColor(Color.WHITE) // Cambiado a blanco
        textView?.setTextColor(Color.BLACK)
    }
    fun comprobarJugada(textViewElegida: TextView) {
        val acierto = textViewElegida.text.equals(respuestaCorrecta)

        if (acierto) {
            aciertos = aciertos!! + 1
            colorRespuesta(textViewElegida, true)
        } else {
            colorRespuesta(textViewElegida, false)
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

        // Definir el color de fondo a verde o rojo
        val colorDestino = if (esCorrecto) 0xFF00FF00.toInt() else 0xFFFF0000.toInt()

        // Cambiar inmediatamente el color de fondo
        textView.setBackgroundColor(colorDestino)


    }

    fun irAtras(view: View) {
        onBackPressed()
    }


}
