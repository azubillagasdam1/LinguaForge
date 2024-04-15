package com.example.linguaforge.activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.linguaforge.R
import com.example.linguaforge.activitys.juegos.Juego1Activity
import com.example.linguaforge.activitys.juegos.Juego2Activity

class JugarActivity : AppCompatActivity() {
    private var imageModo1: ImageView? = null
    private var imageModo2: ImageView? = null
    private var imageModo3: ImageView? = null
    private var imageModo4: ImageView? = null
    private var clave: String? = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_jugar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        clave = intent.getStringExtra("clave") ?: ""
        imageModo1 = findViewById(R.id.imageModo1)
        imageModo2 = findViewById(R.id.imageModo2)
        imageModo3 = findViewById(R.id.imageModo3)
        imageModo4 = findViewById(R.id.imageModo4)

        setScaleAnimation(imageModo1!!)
        setScaleAnimation(imageModo2!!)
        setScaleAnimation(imageModo3!!)
        setScaleAnimation(imageModo4!!)

    }

    @SuppressLint("ClickableViewAccessibility")
    fun setScaleAnimation(imageView: ImageView) {
        imageView.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Aumentar el tamaño
                    view.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Volver al tamaño original
                    val mediaPlayer = MediaPlayer.create(this, R.raw.click_go_sound)
                    mediaPlayer.start()
                    view.animate().scaleX(0.8f).scaleY(0.8f).setDuration(150).start()
                }
            }
            false // Permitir que el evento continúe su propagación
        }
    }


    fun irModo1(view: View) {

        val intent = Intent(this, Juego1Activity::class.java)
        intent.putExtra("clave",  clave)
        startActivity(intent)
    }
    fun irModo2(view: View) {
        val intent = Intent(this, Juego2Activity::class.java)
        intent.putExtra("clave",  clave)
        startActivity(intent)
    }
    fun irModo3(view: View) {}
    fun irModo4(view: View) {}
    fun irAtras(view: View) {
        onBackPressed()
    }
}