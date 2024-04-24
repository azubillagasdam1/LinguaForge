package com.example.linguaforge.activitys

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaforge.ItemPalabra
import com.example.linguaforge.MyPalabraAdapter
import com.example.linguaforge.R
import com.example.linguaforge.fragments.EditarPalabraFragment
import com.example.linguaforge.models.db.FirebaseDB
import com.example.linguaforge.models.utils.Utils
import com.example.linguaforge.models.utils.UtilsDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PalabrasActivity : AppCompatActivity() {

    private var anadirButton: ImageView? = null
    private var jugarButton: ImageView? = null
    private var recyclerView: RecyclerView? = null
    private var fondo: LinearLayout? = null
    private lateinit var textViewTitulo: TextView
    private lateinit var textViewIzquierdo: TextView
    private lateinit var textViewDerecho: TextView
    private lateinit var itemPalabra: List<ItemPalabra>
    private lateinit var idioma1: String
    private lateinit var idioma2: String
    private lateinit var clave: String
    private var ordenacion: Int =
        1 //0-1 default, 2 a-z original, 3 a-ztraduccion,4 a-z original-desc, 5 a-ztraduccion-desc,


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_palabras)

        anadirButton = findViewById(R.id.buttonAnadir)
        jugarButton = findViewById(R.id.buttonJugar)
        fondo = findViewById(R.id.fondoFlecha)
        idioma1 = intent.getStringExtra("idioma1") ?: ""
        idioma2 = intent.getStringExtra("idioma2") ?: ""
        clave = idioma1 + "-" + idioma2


        textViewTitulo = findViewById<TextView>(R.id.textViewTitulo)
        textViewIzquierdo = findViewById<TextView>(R.id.textViewIzquierdo)
        textViewDerecho = findViewById<TextView>(R.id.textViewDerecho)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        crearRecyclerView(this, ordenacion)

        jugarButton?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Acciones cuando el botón es presionado
                irJugar()
            }
            true // Retorna true para indicar que el evento ha sido manejado
        }


        anadirButton?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_UP -> {
                    irAnadir()
                    true
                }

                else -> false
            }
        }


        val spinner: Spinner = findViewById(R.id.filtroSpinner)
// Valores que ve el usuario
        val nombresOrdenacion =
            arrayOf("Inserción", "Original A-Z", "Traducción A-Z", "Original Z-A", "Traducción Z-A")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresOrdenacion)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

// Maneja la selección del usuario
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                // Obtiene el índice seleccionado y lo usa para determinar la ordenación
                val seleccionado = position + 1  // Ajusta según la correspondencia con los números
                ordenacion = seleccionado
                onResume()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Acción para cuando no se selecciona nada
            }
        }
        textViewTitulo.text = textViewTitulo.text.toString() + " " +Utils.getFlagEmoji(idioma2)
        ponerFondo()
    }

    private fun irAnadir() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.click_go_sound)
        mediaPlayer.start()
        val intent = Intent(this, TraductorActivity::class.java)
        intent.putExtra("idioma1", idioma1)
        intent.putExtra("idioma2", idioma2)
        intent.putExtra("clave", clave)
        startActivity(intent)
    }

    private fun irJugar() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.click_go_sound)
        mediaPlayer.start()
        val intent = Intent(this, JugarActivity::class.java)
        intent.putExtra("clave", clave)
        startActivity(intent)

    }

    private fun ponerFondo() {
        // Lanza una corutina en el contexto de la UI
        lifecycleScope.launch {
            // Haz la llamada a la base de datos en un hilo secundario y espera el resultado
            val numeroElementos = withContext(Dispatchers.IO) {
                UtilsDB.getPalabras()?.find { it.containsKey(clave) }?.get(clave)?.size ?: 0
            }

            // Una vez que tienes el resultado, actualiza la UI en el hilo principal
            if (numeroElementos == 0) {
                fondo?.visibility = View.VISIBLE
                textViewIzquierdo.visibility = View.GONE
                textViewDerecho.visibility = View.GONE
            } else {
                fondo?.visibility = View.GONE
                textViewIzquierdo.visibility = View.VISIBLE
                textViewDerecho.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Este método se llama cada vez que la actividad entra en el primer plano.
        crearRecyclerView(this, ordenacion)
    }
    fun recargar(){
        onResume()
    }


    private fun crearRecyclerView(context: Context, modoOrdenacion: Int) {
        lifecycleScope.launch {
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView?.layoutManager = LinearLayoutManager(context)

            var clave = idioma1 + "-" + idioma2
            println(clave)
            val palabrasList = UtilsDB.getPalabras() ?: listOf()

            val listaDePalabrasBruta =
                palabrasList.find { it.containsKey(clave) }?.get(clave) ?: listOf()

            // Determinar el orden de la lista según modoOrdenacion
            val listaDePalabras = when (modoOrdenacion) {
                2 -> listaDePalabrasBruta.sortedBy { it[0] }
                3 -> listaDePalabrasBruta.sortedBy { it[1] }
                4 -> listaDePalabrasBruta.sortedByDescending { it[0] }
                5 -> listaDePalabrasBruta.sortedByDescending { it[1] }
                else -> listaDePalabrasBruta
            }

            itemPalabra = listaDePalabras.map {
                ItemPalabra(
                    clave,
                    it[0], // Palabra
                    it[1] // Traducción
                )
            }

            recyclerView?.adapter = MyPalabraAdapter(itemPalabra, object : MyPalabraAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    onItemClicked(position)
                }

                override fun onItemLongClick(position: Int) {
                    onItemLongClicked(position)
                }
            })
        }
    }


    private fun onItemClicked(position: Int) {
        val mediaPlayer = MediaPlayer.create(this, R.raw.click_go_sound)
        mediaPlayer.start()

        val item = itemPalabra[position]
        var originalSeleccionada = item.original
        var traduccionSeleccionada = item.traduccion

        val fragmentManager = supportFragmentManager
        EditarPalabraFragment(this,position,clave, originalSeleccionada,traduccionSeleccionada).show(fragmentManager, "EditarPalabraFragment")


    }

    private fun onItemLongClicked(position: Int) {
        val item = itemPalabra[position]


        val itemView = recyclerView?.findViewHolderForAdapterPosition(position)?.itemView
        itemView?.setBackgroundColor(Color.parseColor("#ADD8E6")) // Cambiar el color cuando se mantiene presionado

        // Crear un AlertDialog para confirmar la eliminación
        val dialog = AlertDialog.Builder(this)
            .setTitle("Eliminar biblioteca")
            .setMessage("¿Desea eliminar esta biblioteca: ${item.traduccion}?")
            .setPositiveButton("Eliminar") { dialog, which ->
                // SI
                Toast.makeText(this, "${item.traduccion} eliminado", Toast.LENGTH_SHORT).show()
                Utils.eliminarPalabra(clave, position)
                Utils.recargarActividad(this)
            }
            // NO
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnDismissListener {
            // Restablecer el drawable de fondo cuando el diálogo se cierra
            itemView?.setBackgroundResource(R.drawable.style_item)
        }

        dialog.show()
    }


    fun cerrarSesion(view: View) {
        val mediaPlayer = MediaPlayer.create(this, R.raw.click_back_sound)
        mediaPlayer.start()
        FirebaseDB.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun irAtras(view: View) {
        val mediaPlayer = MediaPlayer.create(this, R.raw.click_back_sound)
        mediaPlayer.start()
        val intent = Intent(this, IdiomaActivity::class.java)
        startActivity(intent)
        finish()
        onBackPressed()
    }


}
