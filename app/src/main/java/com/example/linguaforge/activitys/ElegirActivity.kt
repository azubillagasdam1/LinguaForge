package com.example.linguaforge.activitys

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaforge.Item
import com.example.linguaforge.MyAdapter
import com.example.linguaforge.R
import com.example.linguaforge.fragments.CrearIdiomaFragment
import com.example.linguaforge.models.db.FirebaseDB
import com.example.linguaforge.models.utils.UtilsDB
import kotlinx.coroutines.runBlocking

class ElegirActivity : AppCompatActivity() {
    private var anadirButton: Button? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var items: List<Item>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_elegir)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        crearRecyclerView(this)

        anadirButton = findViewById<Button>(R.id.anadirBotton)
        anadirButton?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_UP -> {
                    val fragmentManager = supportFragmentManager
                    CrearIdiomaFragment(this).show(fragmentManager, "CrearIdiomaFragment")
                    true
                }
                else -> false
            }
        }
    }

    private fun crearRecyclerView(context: Context) = runBlocking {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        val idiomasList = UtilsDB.getIdiomas() ?: listOf()
        items = idiomasList.map { idiomaMap ->
            Item(
                idiomaMap["titulo"] ?: "Título predeterminado",
                idiomaMap["subtitulo"] ?: "Subtítulo predeterminado",
                idiomaMap["idioma"] ?: "Idioma predeterminado"
            )
        }

        recyclerView?.adapter = MyAdapter(items, object : MyAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                onItemClicked(position)
            }

            override fun onItemLongClick(position: Int) {
                onItemLongClicked(position)
            }
        })
    }

    private fun onItemClicked(position: Int) {
        val item = items[position]
        Toast.makeText(this, "Tocado: ${item.title}", Toast.LENGTH_SHORT).show()
        Log.d("ElegirActivity", "Item en posición $position fue tocado.")
    }

    private fun onItemLongClicked(position: Int) {
        val item = items[position]
        Toast.makeText(this, "Mantenido: ${item.title}", Toast.LENGTH_SHORT).show()
        Log.d("ElegirActivity", "Item en posición $position fue mantenido.")
    }

    fun cerrarSesion(view: View) {
        FirebaseDB.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun cerrarFragment(view: View) {
        val fragment = supportFragmentManager.findFragmentByTag("CrearIdiomaFragment")
        if (fragment is CrearIdiomaFragment) {
            fragment.dismiss()
        }
    }
}

