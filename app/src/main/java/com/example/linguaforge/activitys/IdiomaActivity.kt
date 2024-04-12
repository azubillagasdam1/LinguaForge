package com.example.linguaforge.activitys

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.linguaforge.ItemIdioma
import com.example.linguaforge.MyIdiomaAdapter
import com.example.linguaforge.R
import com.example.linguaforge.fragments.CrearIdiomaFragment
import com.example.linguaforge.fragments.EditarPalabraFragment
import com.example.linguaforge.models.db.FirebaseDB
import com.example.linguaforge.models.utils.Utils
import com.example.linguaforge.models.utils.UtilsDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class IdiomaActivity : AppCompatActivity() {
    private var anadirButton: Button? = null
    private var fondo: LinearLayout? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var itemIdiomas: List<ItemIdioma>


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_elegir_idioma)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        crearRecyclerView(this)
        fondo = findViewById(R.id.fondoFlecha)
        anadirButton = findViewById(R.id.anadirBotton)
        anadirButton?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start()
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val mediaPlayer = MediaPlayer.create(this, R.raw.click_go_sound)
                    mediaPlayer.start()

                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                    if (event.action == MotionEvent.ACTION_UP) {
                        val fragmentManager = supportFragmentManager
                        CrearIdiomaFragment(this).show(fragmentManager, "CrearIdiomaFragment")
                    }
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
                UtilsDB.getIdiomas()?.size ?: 0
            }

            // Una vez que tienes el resultado, actualiza la UI en el hilo principal
            if (numeroElementos == 0) {
                fondo?.visibility = View.VISIBLE
            } else {
                fondo?.visibility = View.GONE
            }
        }
    }


    private fun crearRecyclerView(context: Context) {
        lifecycleScope.launch {
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView?.layoutManager = LinearLayoutManager(context)

            val idiomasList = UtilsDB.getIdiomas() ?: listOf()
            // Establecer recyclerViewVacio en false si idiomasList tiene elementos

            itemIdiomas = idiomasList.map { idiomaMap ->
                ItemIdioma(
                    idiomaMap["titulo"] ?: "Título predeterminado",
                    idiomaMap["subtitulo"] ?: "Subtítulo predeterminado",
                    idiomaMap["idiomaOrigen"] ?: "Idioma1 predeterminado",
                    idiomaMap["idiomaResultado"] ?: "Idioma2 predeterminado"
                )
            }

            recyclerView?.adapter =
                MyIdiomaAdapter(itemIdiomas, object : MyIdiomaAdapter.OnItemClickListener {
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
        val item = itemIdiomas[position]
        val mediaPlayer = MediaPlayer.create(this, R.raw.click_go_sound)
        mediaPlayer.start()
        Toast.makeText(this, "Tocado: ${item.title}", Toast.LENGTH_SHORT).show()
        Log.d("ElegirActivity", "Item en posición $position fue tocado.")
        val intent = Intent(this, PalabrasActivity::class.java)
        intent.putExtra("idioma1", Utils.getCountryCode(item.idioma1))
        intent.putExtra("idioma2", Utils.getCountryCode(item.idioma2))
        startActivity(intent)
        finish()

    }

    private fun onItemLongClicked(position: Int) {
        val item = itemIdiomas[position]
        Toast.makeText(this, "Mantenido: ${item.title}", Toast.LENGTH_SHORT).show()
        Log.d("ElegirActivity", "Item en posición $position fue mantenido.")

        val itemView = recyclerView?.findViewHolderForAdapterPosition(position)?.itemView
        // Cambiar el fondo al drawable
        itemView?.setBackgroundColor(Color.parseColor("#ADD8E6"))

        val dialog = AlertDialog.Builder(this)
            .setTitle("Eliminar biblioteca")
            .setMessage("¿Desea eliminar esta biblioteca: ${item.title}?")
            .setPositiveButton("Eliminar") { dialog, which ->
                // SI
                Toast.makeText(this, "${item.title} eliminado", Toast.LENGTH_SHORT).show()
                Utils.eliminarIdioma(position)
                Utils.recargarActividad(this)
            }
            // NO
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnDismissListener {
            // Restablece el fondo al original cuando el diálogo se cierra
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

    fun cerrarFragment(view: View) {
        val mediaPlayer = MediaPlayer.create(this, R.raw.click_back_sound)
        mediaPlayer.start()
        val fragment = supportFragmentManager.findFragmentByTag("CrearIdiomaFragment")
        if (fragment is CrearIdiomaFragment) {
            fragment.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        // Este método se llama cada vez que la actividad entra en el primer plano.
        crearRecyclerView(this)
    }

}

