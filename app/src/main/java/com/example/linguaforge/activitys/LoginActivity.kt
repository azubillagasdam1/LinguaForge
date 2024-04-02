package com.example.linguaforge.activitys

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private val RC_SIGN_IN = 9001
    private lateinit var mediaPlayer: MediaPlayer

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

        // Comprobar si el usuario ya está autenticado con Firebase al iniciar la actividad
        if (FirebaseDB.getInstanceFirebase().currentUser != null) {
            // El usuario ya está autenticado, redirigir a ElegirActivity
            val intent = Intent(this, ElegirIdiomaActivity::class.java)
            startActivity(intent)
            finish()  // Finalizar LoginActivity para que el usuario no pueda volver a ella presionando el botón Atrás
        }
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
    fun iniciarSesionGoogle(view: View?) {
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

                                val intent = Intent(this, ElegirIdiomaActivity::class.java)
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
                                val intent = Intent(this, ElegirIdiomaActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                }
            }
    }


}