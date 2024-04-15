    package com.example.linguaforge.activitys.juegos

    import android.graphics.Color
    import android.media.MediaPlayer
    import android.os.Bundle
    import android.view.Gravity
    import android.view.View
    import android.widget.LinearLayout
    import android.widget.TextView
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.util.TypedValueCompat.dpToPx
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import com.example.linguaforge.R
    import com.example.linguaforge.models.utils.Utils
    import kotlinx.coroutines.runBlocking

    class Juego2Activity : AppCompatActivity() {
        private lateinit var clave: String
        private var palabras: List<List<String>>? = null
        private var textViewPalabra: TextView? = null
        private var jugadas: Int? = 0 // Variable para almacenar cantidad aciertos
        private var aciertos: Int? = 0 // Variable para almacenar cantidad aciertos
        private var respuestaCorrecta: String? = null
        private var respuestarevuelta: String? = null

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
            obtenerPalabras()

            cargarPalabra()

            generarHuecos(respuestaCorrecta!!, 0)
            generarCulebra(respuestaCorrecta!!)

        }


        private fun obtenerPalabras() = runBlocking {
            palabras = Utils.obtenerPalabrasPorClave(clave)?.shuffled()

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
                    setBackgroundResource(R.drawable.degradado_fondo)
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
            contenedor.orientation = LinearLayout.HORIZONTAL  // Asegúrate de que el contenedor es horizontal

            // Utiliza directamente la palabra desordenada recibida como parámetro
            for (i in palabraDesordenada.indices) {
                val textView = TextView(this).apply {
                    text = palabraDesordenada[i].toString().uppercase()  // Usa una letra desordenada
                    textSize = 30f
                    setPadding(16, 16, 16, 16)  // Ajusta el padding según necesites
                    gravity = Gravity.CENTER
                    setBackgroundResource(R.drawable.degradado_fondo)  // Usa el drawable de degradado
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        50.dpToPx()
                    ).also {
                        it.weight = 1f
                        it.setMargins(
                            4.dpToPx(),
                            4.dpToPx(),
                            4.dpToPx(),
                            4.dpToPx()
                        )  // Añade márgenes para la separación
                    }
                    isClickable = true
                    setOnClickListener {
                        comprobarJugada(text.toString().uppercase())  // Asegura que se compara en mayúsculas
                    }
                }
                contenedor.addView(textView)
            }
        }


        private fun comprobarJugada(letra: String) {
            if (aciertos!! < respuestaCorrecta!!.length && letra.equals(
                    respuestaCorrecta!![aciertos!!].toString(),
                    ignoreCase = true
                )
            ) {
                // Acción para cuando la letra es correcta
                val mediaPlayer = MediaPlayer.create(this, R.raw.correct_sound)
                mediaPlayer.start()
                aciertos = aciertos!! + 1  // Incrementa el contador de aciertos
                generarHuecos(respuestaCorrecta!!, aciertos!!)

                // Eliminar la letra acertada de la respuesta revuelta, asegurando que se compara en mayúsculas
                respuestarevuelta = respuestarevuelta!!.filterNot { it.toString().equals(letra, ignoreCase = true) }
                if (aciertos!! < respuestaCorrecta!!.length) {
                    generarCulebra(respuestarevuelta!!)
                } else {
                    // Acción para cuando se finaliza la palabra
                    finish()
                }
            } else {
                // Acción para cuando la letra es incorrecta
                val mediaPlayer = MediaPlayer.create(this, R.raw.error_sound)
                mediaPlayer.start()
            }
        }


        private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()


    }