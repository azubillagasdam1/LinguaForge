package com.example.linguaforge.activitys

import android.os.Bundle
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
    private lateinit var clave: String
    private var palabras: List<List<String>>? = null
    private var palabra: TextView? = null
    private var pregunta: TextView? = null
    private var respuesta1: TextView? = null
    private var respuesta2: TextView? = null
    private var respuesta3: TextView? = null
    private var respuesta4: TextView? = null

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
        cargarPalabras()
    }



    private fun obtenerPalabras() = runBlocking {
        palabras = Utils.obtenerPalabrasPorClave(clave)

    }
    private fun cargarPalabras() {
        // Verificar que la lista no sea nula y contenga elementos
        if (palabras != null && palabras!!.isNotEmpty()) {
            // Crear una copia de la lista original y mezclarla
            val palabrasMezcladas = palabras!!.toMutableList().shuffled()

            // Seleccionar la primera "traducción" y establecerla como texto de pregunta
            val traduccionSeleccionada = palabrasMezcladas.first().getOrNull(1) ?: ""
            palabra?.text = traduccionSeleccionada
            pregunta?.text = "¿Cual de estas es "+traduccionSeleccionada+" ?"

            // Seleccionar la "palabra" asociada a la traducción seleccionada
            val palabraSeleccionada = palabrasMezcladas.first().getOrNull(0) ?: ""

            // Crear una lista con la palabra seleccionada y añadir tres palabras aleatorias
            val opciones = mutableListOf(palabraSeleccionada)
            while (opciones.size < 4) {
                val palabraAleatoria = palabrasMezcladas[Random.nextInt(palabrasMezcladas.size)].first()
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
        }
    }
}