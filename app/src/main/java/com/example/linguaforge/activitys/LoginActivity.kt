package com.example.linguaforge.activitys

import UserDao

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.linguaforge.R
import com.example.linguaforge.models.db.FirebaseDB
import com.example.linguaforge.models.entity.User
import com.example.linguaforge.models.utils.HashUtils
import com.example.linguaforge.models.utils.Utils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import java.time.LocalDateTime
import java.util.Random



class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private val RC_SIGN_IN = 9001
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var logoImageView: ImageView
    private lateinit var subtituloTextView: TextView
    private lateinit var googleImageView: ImageView
    private lateinit var flechaCurvaImageView: ImageView
    private lateinit var iniciaSesionTextview: TextView
    private lateinit var titleTextView: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.click_sound)
        logoImageView = findViewById(R.id.logoImageView)
        subtituloTextView = findViewById(R.id.subtitleTextView)
        googleImageView = findViewById(R.id.googleSignInImageView)
        flechaCurvaImageView = findViewById(R.id.flechaCurvaImageView)
        iniciaSesionTextview = findViewById(R.id.iniciaSesionTextview)
        titleTextView = findViewById(R.id.titleTextView)

        // Comprobar si el usuario ya está autenticado con Firebase al iniciar la actividad
        if (FirebaseDB.getInstanceFirebase().currentUser != null) {
            // El usuario ya está autenticado, redirigir a ElegirActivity
            val intent = Intent(this, IdiomaActivity::class.java)
            startActivity(intent)
            finish()  // Finalizar LoginActivity para que el usuario no pueda volver a ella presionando el botón Atrás
        }

        animacionFondo()
        animacionMovimientoImageView(googleImageView)
        startErrorAnimation(subtituloTextView)

        setupInitialAnimations()
        setupGoogleImageViewInteractions()
    }

    private fun setupInitialAnimations() {
        val fadeInAnimator = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f).apply {
            duration = 5000
        }
        val scaleXAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleX", 2f, 1f).apply {
            duration = 3000
        }
        val scaleYAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleY", 2f, 1f).apply {
            duration = 3000
        }

        AnimatorSet().apply {
            playTogether(fadeInAnimator, scaleXAnimator, scaleYAnimator)
            start()
        }

        val fadeInAnimatorTitle = ObjectAnimator.ofFloat(titleTextView, "alpha", 0f, 1f).apply {
            duration = 5000
        }
        val scaleXAnimatorTitle = ObjectAnimator.ofFloat(titleTextView, "scaleX", 2f, 1f).apply {
            duration = 3000
        }
        val scaleYAnimatorTitle = ObjectAnimator.ofFloat(titleTextView, "scaleY", 2f, 1f).apply {
            duration = 3000
        }

        AnimatorSet().apply {
            playTogether(fadeInAnimatorTitle, scaleXAnimatorTitle, scaleYAnimatorTitle)
            start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGoogleImageViewInteractions() {
        googleImageView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()

                    if (event.action == MotionEvent.ACTION_UP) {
                        iniciarSesionGoogle()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun startErrorAnimation(view: View) {
        view.setBackgroundResource(R.drawable.style_bloque_error_letras)
        val shakeAnimation = ObjectAnimator.ofFloat(view, "translationX", 0f, 10f, -10f, 10f, -10f, 0f).apply {
            duration = 100
        }
        shakeAnimation.start()

        Handler(Looper.getMainLooper()).postDelayed({
            view.setBackgroundResource(R.drawable.style_bloque_letras)
            fadeInBackground(view)
        }, 100)
    }

    private fun fadeInBackground(view: View) {
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            duration = 500
        }
        fadeIn.start()
    }


    /**
     * Función que sale de la aplicación
     *
     * @param view Vista del botón.
     */
    fun irSalir(view: View) {
        Utils.salirAplicacion(this)
    }

    /**
     * Función que retrocede a la ventana anterior
     */
    override fun onBackPressed() {
        Utils.salirAplicacion(this)
        super.onBackPressed()
    }

    /**
     * Función que realiza el proceso de inicio de sesión con Google.
     *
     * @param view Vista del botón.
     */
    fun iniciarSesionGoogle() {
        try {
            signInWithGoogle()
        } catch (e: Exception) {
            Log.e(TAG, "Error durante el inicio de sesión con Google", e)
            Toast.makeText(
                this,
                "Error durante el inicio de sesión con Google",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    /**
     * Función que inicia el proceso de inicio de sesión con Google.
     */
    private fun signInWithGoogle() {
        // Configuración de inicio de sesión con Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("684238212774-irv66cbobfsts532pkgf4c2sr00o4gf2.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Desvincular la cuenta de Google actual antes de iniciar una nueva sesión.
        googleSignInClient.signOut().addOnCompleteListener {
            // Iniciar el proceso de inicio de sesión una vez que la cuenta ha sido desvinculada.
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }


    /**
     * Sobrescribe el método onActivityResult para manejar el resultado de la actividad de inicio de sesión con Google.
     *
     * @param requestCode Código de solicitud pasado a startActivityForResult.
     * @param resultCode  Resultado de la actividad que indica si la operación fue exitosa.
     * @param data        Datos asociados con el resultado de la actividad.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Verificar si el código de solicitud coincide con el código de inicio de sesión con Google
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    /**
     * Maneja el resultado de la tarea de inicio de sesión con Google.
     *
     * @param task Tarea que contiene el resultado de la autenticación con Google.
     * Se intenta obtener la cuenta de Google desde el resultado de la tarea y autenticar
     * con Firebase utilizando las credenciales de Google. En caso de un error (por ejemplo,
     * ApiException), se registra un mensaje de error y se muestra un Toast informando
     * al usuario sobre el fallo en el inicio de sesión con Google.
     *
     * @see ApiException
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            // Obtener la cuenta de Google desde el resultado de la tarea
            val account = task.getResult(ApiException::class.java)
            // Autenticar con Firebase usando la cuenta de Google
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            // Manejar el fallo en el inicio de sesión con Google
            Log.w(TAG, "Fallo en el inicio de sesión con Google", e)
            println(e)
            Toast.makeText(
                this,
                "Fallo en el inicio de sesión con Google",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Autentica al usuario con Firebase utilizando las credenciales de Google.
     *
     * @param account Cuenta de Google utilizada para la autenticación.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        // Obtener credenciales de autenticación de Google
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        // Autenticar con Firebase usando las credenciales de Google
        FirebaseDB.getInstanceFirebase().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Obtener la cuenta de usuario de Firebase
                    val googleName = account?.displayName
                    val googleEmail = account?.email
                    val emailEncriptado = googleEmail?.let { HashUtils.sha256(it.lowercase()) }

                    val usersRef = FirebaseDB.getInstanceFirestore().collection("usuarios")

                    // Verificar si el correo electrónico está presente en la colección de usuarios
                    usersRef.whereEqualTo("email", emailEncriptado).get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {

                                val intent = Intent(this, IdiomaActivity::class.java)
                                startActivity(intent)
                                finish()
                                mediaPlayer.start()
                            } else {

                                Log.d(TAG, "Nombre de Google: $googleName")
                                Log.d(TAG, "Correo electrónico de Google: $googleEmail")
                                val fechaRegistro = LocalDateTime.now()

                                val user = User(
                                    email = emailEncriptado,
                                    name = googleName,
                                    correo = googleEmail?.lowercase(),
                                    fechaRegistro = fechaRegistro
                                )


                                UserDao.addUser(user)
                                val intent = Intent(this, IdiomaActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                }
            }
    }

    fun animacionMovimientoImageView(imageView: View) {
        // Establecer los puntos de pivote en el centro del botón para la animación
        imageView.pivotX = imageView.width / 2f
        imageView.pivotY = imageView.height / 2f

        val aumentarX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.1f)
        val aumentarY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.1f)
        aumentarX.duration = 5000
        aumentarY.duration = 5000

        val disminuirX = ObjectAnimator.ofFloat(imageView, "scaleX", 1.1f, 1f)
        val disminuirY = ObjectAnimator.ofFloat(imageView, "scaleY", 1.1f, 1f)
        disminuirX.duration = 5000
        disminuirY.duration = 5000

        val aumentarSet = AnimatorSet().apply {
            playTogether(aumentarX, aumentarY)
        }

        val disminuirSet = AnimatorSet().apply {
            playTogether(disminuirX, disminuirY)
        }

        val overallSet = AnimatorSet().apply {
            playSequentially(aumentarSet, disminuirSet)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    // Reiniciar la animación al finalizar
                    this@apply.start()
                }
            })
        }

        overallSet.start()
    }

    fun animacionFondo() {
        val fondos = listOf(
            findViewById<ImageView>(R.id.fondoletras1),
            findViewById<ImageView>(R.id.fondoletras2),
            findViewById<ImageView>(R.id.fondoletras3),
            findViewById<ImageView>(R.id.fondoletras4)
        )

        val random = Random()

        fondos.forEachIndexed { index, fondo ->
            // Establecer la opacidad inicial a 0
            fondo.alpha = 0f
            animarAparecer(fondo, random, index * 1000L) // Comienza con la animación de aparición.
        }
    }

    fun animarFondo(fondo: ImageView, random: Random, startDelay: Long) {
        val duracionDesvanecer = 1000L + random.nextInt(2000)

        ObjectAnimator.ofFloat(fondo, "alpha", 1f, 0f).apply {
            duration = duracionDesvanecer
            this.startDelay = startDelay
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animarAparecer(fondo, random, 0) // Sin desfase para la animación de aparición.
                }
            })
            start()
        }
    }

    fun animarAparecer(fondo: ImageView, random: Random, startDelay: Long) {
        val duracionAparecer = 5000L + random.nextInt(2000)

        ObjectAnimator.ofFloat(fondo, "alpha", 0f, 1f).apply {
            duration = duracionAparecer
            this.startDelay = startDelay
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animarFondo(fondo, random, 0) // Al finalizar, comienza de nuevo sin desfase.
                }
            })
            start()
        }
    }
}