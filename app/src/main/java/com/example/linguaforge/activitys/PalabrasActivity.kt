package com.example.linguaforge.activitys

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
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
import com.example.linguaforge.models.db.FirebaseDB
import com.example.linguaforge.models.utils.Utils
import com.example.linguaforge.models.utils.UtilsDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PalabrasActivity : AppCompatActivity() {
    private var anadirButton: Button? = null
    private var recyclerView: RecyclerView? = null
    private var fondo: LinearLayout? = null
    private lateinit var itemPalabra: List<ItemPalabra>
    private lateinit var idioma1: String
    private lateinit var idioma2: String
    private lateinit var clave: String
    private lateinit var textViewIzquierdo: TextView
    private lateinit var textViewDerecho: TextView


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_palabras)

        fondo = findViewById(R.id.fondoFlecha)
        idioma1 = intent.getStringExtra("idioma1") ?: ""
        idioma2 = intent.getStringExtra("idioma2") ?: ""
        clave = idioma1 + "-" + idioma2
        textViewIzquierdo = findViewById<TextView>(R.id.textViewIzquierdo)
        textViewDerecho = findViewById<TextView>(R.id.textViewDerecho)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        crearRecyclerView(this)

        anadirButton = findViewById(R.id.anadirBotton)
        anadirButton?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_UP -> {

                    val fragmentManager = supportFragmentManager
                    val intent = Intent(this, TraductorActivity::class.java)
                    intent.putExtra("idioma1", idioma1)
                    intent.putExtra("idioma2", idioma2)
                    intent.putExtra("clave", clave)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
        ponerFondo()
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
                textViewIzquierdo?.visibility = View.GONE
                textViewDerecho?.visibility = View.GONE
            } else {
                fondo?.visibility = View.GONE
                textViewIzquierdo?.visibility = View.VISIBLE
                textViewDerecho?.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Este método se llama cada vez que la actividad entra en el primer plano.
        crearRecyclerView(this)
    }


    private fun crearRecyclerView(context: Context) = runBlocking {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        var clave = idioma1 + "-" + idioma2
        println(clave)
        val palabrasList = UtilsDB.getPalabras() ?: listOf()

        // Asumiendo que `clave` es la clave para buscar en el mapa de idiomas.
        val listaDePalabras = palabrasList.find { it.containsKey(clave) }?.get(clave) ?: listOf()

        itemPalabra = listaDePalabras.map {
            ItemPalabra(
                clave,
                it[0], // Palabra
                it[1] // Traducción
                // Clave del idioma
            )
        }

        recyclerView?.adapter =
            MyPalabraAdapter(itemPalabra, object : MyPalabraAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    onItemClicked(position)
                }

                override fun onItemLongClick(position: Int) {
                    onItemLongClicked(position)
                }
            })
    }


    private fun onItemClicked(position: Int) {
        val item = itemPalabra[position]
        Toast.makeText(this, "Tocado: ${item.traduccion}", Toast.LENGTH_SHORT).show()
        Log.d("ElegirActivity", "Item en posición $position fue tocado.")

    }

    private fun onItemLongClicked(position: Int) {
        val item = itemPalabra[position]
        Toast.makeText(this, "Mantenido: ${item.traduccion}", Toast.LENGTH_SHORT).show()
        Log.d("ElegirActivity", "Item en posición $position fue mantenido.")

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
        FirebaseDB.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun irAtras(view: View) {
        onBackPressed()
    }


}
