    package com.example.linguaforge.activitys.juegos

    import android.animation.ObjectAnimator
    import android.graphics.Color
    import android.media.MediaPlayer
    import android.os.Bundle
    import android.os.Handler
    import android.os.Looper
    import android.view.Gravity
    import android.view.View
    import android.widget.ImageView
    import android.widget.LinearLayout
    import android.widget.TextView
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.lifecycle.lifecycleScope
    import com.example.linguaforge.R
    import com.example.linguaforge.models.utils.Utils
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.runBlocking

    class Juego2Activity : AppCompatActivity() {
        private var marcador: TextView? = null
        private var marcadorVidas: TextView? = null
        private var corazonImageView: ImageView? = null
        private final var MAXIMO_VIDAS: Int = 3
        private lateinit var clave: String
        private var palabras: List<List<String>>? = null
        private var textViewPalabra: TextView? = null
        private var jugadas: Int? = 0 // Variable para almacenar cantidad aciertos
        private var aciertos: Int? = 0 // Variable para almacenar cantidad aciertos
        private var intentos: Int? = 0 // Variable para almacenar cantidad aciertos
        private var respuestaCorrecta: String? = null
        private var respuestarevuelta: String? = null
        private var textViewReferencias = mutableListOf<TextView>()


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_juego2)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            clave = intent.getStringExtra("clave") ?: ""
            textViewPalabra = findViewById(R.id.palabraTextView)
            marcador = findViewById(R.id.marcadorTextView)
            marcadorVidas = findViewById(R.id.marcadorVidasTextView)
            corazonImageView = findViewById(R.id.corazonImageView)
            obtenerPalabras()

            cargarPalabra()

            generarHuecos(respuestaCorrecta!!, 0)
            generarCulebra(respuestarevuelta!!)
            actualizarMarcador()

        }


        private fun actualizarMarcador() {
            val totalPalabras = palabras?.size ?: 0
            val totalAciertos = aciertos ?: 0
            val totalIntentosRealizadas = intentos ?: 0


            val totalFallos = totalIntentosRealizadas - totalAciertos


            marcador?.text = "$jugadas / $totalPalabras"

            val vidasRestantes = MAXIMO_VIDAS - totalFallos
            marcadorVidas?.text = "X$vidasRestantes"

            if (totalFallos >= MAXIMO_VIDAS || totalIntentosRealizadas >= totalPalabras) {
                finalizar()
            }
        }

        private fun obtenerPalabras() {
            lifecycleScope.launch {
                palabras = Utils.obtenerPalabrasPorClave(clave)?.shuffled()
                // Puedes actualizar la UI aquí si es necesario, por ejemplo, refrescar un adaptador.

            }
        }


        private fun cargarPalabra() {
            if (palabras != null && palabras!!.isNotEmpty() && jugadas!! < palabras!!.size) {
                val grupoActual = palabras!![jugadas!!]
                val palabra = grupoActual.getOrNull(0) ?: ""
                val traducida = grupoActual.getOrNull(1) ?: ""
                textViewPalabra?.text = palabra
                respuestaCorrecta = traducida
                respuestarevuelta = respuestaCorrecta!!.toCharArray().toList().shuffled().joinToString("")
                generarCulebra(respuestarevuelta!!)
            }
        }

        private fun generarHuecos(palabra: String, numVisible: Int) {
            val contenedor = findViewById<LinearLayout>(R.id.huecosLinearLayout)
            contenedor.removeAllViews()  // Limpia vistas anteriores si es necesario
            contenedor.orientation =
                LinearLayout.HORIZONTAL  // Asegúrate de que el contenedor es horizontal

            for (i in palabra.indices) {
                // Contenedor para cada letra y su línea subyacente
                val letraContenedor = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.weight = 1f
                        it.setMargins(4.dpToPx(), 4.dpToPx(), 4.dpToPx(), 4.dpToPx())
                    }
                }

                val textView = TextView(this).apply {
                    text = palabra[i].toString().uppercase()  // Usa la letra en orden
                    textSize = 30f
                    gravity = Gravity.CENTER
                    setPadding(16, 16, 16, 16)
                    setBackgroundResource(R.drawable.style_bloque_letras)
                    // Establece la visibilidad del TextView según el índice
                    visibility =
                        if (i < numVisible) View.VISIBLE else View.INVISIBLE  // Usar INVISIBLE para mantener el espacio
                }

                val linea = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5.dpToPx()  // Altura de la línea
                    )
                    setBackgroundColor(Color.BLACK)  // Color de la línea
                }

                letraContenedor.addView(textView)
                letraContenedor.addView(linea)
                contenedor.addView(letraContenedor)
            }
        }


        private fun generarCulebra(palabraDesordenada: String) {
            val contenedor = findViewById<LinearLayout>(R.id.culebraLinearLayout)
            contenedor.removeAllViews()  // Limpia vistas anteriores si es necesario
            textViewReferencias.clear()  // Limpia la lista de referencias de TextView

            for (i in palabraDesordenada.indices) {
                val textView = TextView(this).apply {
                    text = palabraDesordenada[i].toString().uppercase()
                    textSize = 30f
                    setPadding(16, 16, 16, 16)
                    gravity = Gravity.CENTER
                    setBackgroundResource(R.drawable.style_bloque_letras)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        50.dpToPx()
                    ).also {
                        it.weight = 1f
                        it.setMargins(4.dpToPx(), 4.dpToPx(), 4.dpToPx(), 4.dpToPx())
                    }
                    isClickable = true
                    setOnClickListener {
                        comprobarJugada(text.toString().uppercase(), i)
                        actualizarMarcador()
                    }
                }
                contenedor.addView(textView)
                textViewReferencias.add(textView)  // Agrega la referencia a la lista
            }
        }




        private fun comprobarJugada(letra: String, index: Int) {
            intentos = intentos!! + 1
            if (aciertos!! < respuestaCorrecta!!.length && letra.equals(
                    respuestaCorrecta!![aciertos!!].toString(),
                    ignoreCase = true
                )
            ) {
                // Acción para cuando la letra es correcta
                val mediaPlayer = MediaPlayer.create(this, R.raw.correct_sound)
                mediaPlayer.start()
                efectoAcierto(index)
                aciertos = aciertos!! + 1  // Incrementa el contador de aciertos
                generarHuecos(respuestaCorrecta!!, aciertos!!)
                respuestarevuelta = respuestarevuelta!!.removeRange(index, index + 1)
                generarCulebra(respuestarevuelta!!)
                if ((aciertos!! < respuestaCorrecta!!.length).not()) {
                    aciertos = aciertos!! + 1
                    recargarPartida()


                }
            } else {
                // Acción para cuando la letra es incorrecta
                val mediaPlayer = MediaPlayer.create(this, R.raw.error_sound)

                mediaPlayer.start()
                efectoError(index)
            }
        }

        private fun recargarPartida() {
            jugadas = jugadas!! + 1
            intentos = 0
            aciertos = 0
            efectoVerdeTemporal {
                cargarPalabra()
                generarHuecos(respuestaCorrecta!!, aciertos!!)
            }
        }

        private fun efectoVerdeTemporal(onComplete: () -> Unit) {
            val contenedor = findViewById<LinearLayout>(R.id.huecosLinearLayout)

            for (i in 0 until contenedor.childCount) {
                val letraContenedor = contenedor.getChildAt(i) as LinearLayout
                val textView = letraContenedor.getChildAt(0) as TextView  // Asumiendo que el TextView es el primer hijo

                // Cambia el fondo a verde permanentemente
                textView.setBackgroundResource(R.drawable.style_bloque_acierto_letras)
            }

            // Usa un postDelayed para llamar al callback después de 500 ms sin cambiar de nuevo el fondo
            contenedor.postDelayed({
                onComplete()  // Ejecuta las acciones posteriores después del retardo
            }, 500)
        }


        private fun efectoError(index: Int) {
            if (index >= 0 && index < textViewReferencias.size) {
                val textView = textViewReferencias[index]

                // Cambia temporalmente el fondo a rojo
                textView.setBackgroundResource(R.drawable.style_bloque_error_letras)

                // Animación de "vibración" solo en el eje X
                val shakeX = ObjectAnimator.ofFloat(textView, "translationX", 0f, 10f, -10f, 10f, -10f, 0f).apply {
                    duration = 100  // Duración corta para una rápida "vibración"
                }
                shakeX.start()

                // Handler para cambiar el fondo de nuevo al degradado después de una breve exposición del color rojo
                Handler(Looper.getMainLooper()).postDelayed({
                    // Cambia el fondo al degradado original
                    textView.setBackgroundResource(R.drawable.style_bloque_letras)

                    // Animación de fade-in para el degradado
                    val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f).apply {
                        duration = 500  // Duración de la animación de fade-in
                    }
                    fadeIn.start()
                }, 100)  // Retardo para permitir que el color rojo sea visible
            }
        }

        private fun efectoAcierto(index: Int) {
            val contenedor = findViewById<LinearLayout>(R.id.huecosLinearLayout)
            if (index >= 0 && index < contenedor.childCount) {
                val letraContenedor = contenedor.getChildAt(index) as LinearLayout
                val textView = letraContenedor.getChildAt(0) as TextView

                // Cambia temporalmente el fondo a verde
                textView.setBackgroundResource(R.drawable.style_bloque_acierto_letras)

                // Animación de fade-out para el color verde
                val fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f).apply {
                    duration = 1000  // Duración de la animación de fade-out
                    start()
                }

                // Handler para cambiar el fondo de nuevo al degradado después de que el color verde sea visible
                Handler(Looper.getMainLooper()).postDelayed({
                    // Cambia el fondo al degradado original
                    textView.setBackgroundResource(R.drawable.style_bloque_letras)

                    // Animación de fade-in para el degradado
                    val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f).apply {
                        duration = 1000  // Duración de la animación de fade-in
                        start()
                    }
                }, 1000)  // Retardo para permitir que el color verde sea visible
            }
        }





        private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

        private fun finalizar() {
            finish()
        }

        fun irAtras(view: View) {
            onBackPressed()
        }
    }
