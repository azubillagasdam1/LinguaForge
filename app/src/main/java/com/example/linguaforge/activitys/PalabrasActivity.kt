package com.example.linguaforge.activitys

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaforge.ItemPalabra
import com.example.linguaforge.MyIdiomaAdapter
import com.example.linguaforge.MyPalabraAdapter
import com.example.linguaforge.R
import com.example.linguaforge.models.db.FirebaseDB
import com.example.linguaforge.models.utils.UtilsDB
import kotlinx.coroutines.runBlocking

class PalabrasActivity : AppCompatActivity() {
    private var anadirButton: Button? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var itemPalabra: List<ItemPalabra>
    val idioma1 = intent.getStringExtra("idioma1")
    val idioma2 = intent.getStringExtra("idioma2")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_palabras)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        anadirButton = findViewById<Button>(R.id.anadirBotton)
        crearRecyclerView(this)



    }

    private fun crearRecyclerView(context: Context) = runBlocking {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        var clave = idioma1 + "-" + idioma2
        val idiomasList = UtilsDB.getIdiomas() ?: listOf()
        itemPalabra = idiomasList.map { idiomaMap ->
            ItemPalabra(
                idiomaMap["titulo"] ?: "Título predeterminado",
                idiomaMap["subtitulo"] ?: "Subtítulo predeterminado",
                idiomaMap["idioma"] ?: "Idioma predeterminado"
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


    private fun onItemClicked(position: Int) {
        val item = itemPalabra[position]
        Toast.makeText(this, "Tocado: ${item.idioma}", Toast.LENGTH_SHORT).show()
        Log.d("ElegirActivity", "Item en posición $position fue tocado.")

    }

    private fun onItemLongClicked(position: Int) {
        val item = itemPalabra[position]
        Toast.makeText(this, "Mantenido: ${item.idioma}", Toast.LENGTH_SHORT).show()
        Log.d("ElegirActivity", "Item en posición $position fue mantenido.")
    }


    fun cerrarSesion(view: View) {
        FirebaseDB.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}