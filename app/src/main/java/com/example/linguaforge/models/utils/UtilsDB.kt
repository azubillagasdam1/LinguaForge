package com.example.linguaforge.models.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.linguaforge.models.db.FirebaseDB
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await

import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class UtilsDB {
    companion object {
        var firebaseAuth = FirebaseDB.getInstanceFirebase()
        var db = FirebaseDB.getInstanceFirestore()
        var currentUser = firebaseAuth.currentUser
        var email = currentUser?.email?.lowercase()
        private var usersCollection = db.collection("usuarios")
        private var emailEncriptado = HashUtils.sha256(email!!)

        fun actualizarVariables() {
            try {
                firebaseAuth = FirebaseDB.getInstanceFirebase()
                db = FirebaseDB.getInstanceFirestore()
                currentUser = firebaseAuth.currentUser
                email = currentUser?.email?.lowercase()
                usersCollection = db.collection("usuarios")
                emailEncriptado = HashUtils.sha256(email!!)
            } catch (e: NullPointerException) {
                Log.e(ContentValues.TAG, "Error de NullPointerException al actualizar variables: $e")
            }
        }


        suspend fun getIdiomas(): List<Map<String, String>>? {
            actualizarVariables()
            val docRef = db.collection("usuarios").document(emailEncriptado)

            return try {
                val document = docRef.get().await()
                if (document.exists()) {
                    // Aquí se cambia la conversión a List<Map<String, String>>
                    val idiomasList = document.data?.get("idiomas") as? List<Map<String, String>>

                    idiomasList
                } else {
                    println("No such document")
                    null
                }
            } catch (exception: Exception) {
                println("Error getting documents: $exception")
                null
            }
        }
        suspend fun getPalabras(): List<Map<String, List<List<String>>>>? {
            actualizarVariables()
            val docRef = db.collection("usuarios").document(emailEncriptado)

            return try {
                val document = docRef.get().await()
                if (document.exists()) {
                    val palabrasList = document.data?.get("palabras") as? List<Map<String, List<Map<String, String>>>>
                    val result = palabrasList?.map { idiomaMap ->
                        idiomaMap.mapValues { entry ->
                            entry.value.map { listOf(it["palabra"] ?: "", it["traduccion"] ?: "") }
                        }
                    }

                    result
                } else {
                    println("No such document")
                    null
                }
            } catch (exception: Exception) {
                println("Error getting documents: $exception")
                null
            }
        }




        fun setIdioma(idiomas: List<Map<String, String>>) {
            actualizarVariables()
            val data = hashMapOf(
                "idiomas" to idiomas
            )
            usersCollection.document(emailEncriptado).update(data as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Idioma actualizado correctamente")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error al actualizar el idioma", e)
                }
        }

        fun setPalabra(palabras: List<Map<String, List<List<String>>>>) {
            actualizarVariables()
            val palabrasFirestore = palabras.map { idiomaMap ->
                idiomaMap.mapValues { entry ->
                    entry.value.map { hashMapOf("palabra" to it[0], "traduccion" to it[1]) }
                }
            }

            val data = hashMapOf(
                "palabras" to palabrasFirestore
            )

            usersCollection.document(emailEncriptado).update(data as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Palabras actualizadas correctamente")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error al actualizar las palabras", e)
                }
        }

        fun setPalabras(palabras: List<Map<String, List<List<String>>>>) {
            actualizarVariables()

            // Transforma la estructura de palabras para el formato esperado por Firestore
            val palabrasFirestore = palabras.map { mapaPalabras ->
                mapaPalabras.mapValues { entry ->
                    entry.value.map { listaPalabras ->
                        // Asegúrate de que cada sublista tenga al menos dos elementos (palabra y traducción)
                        if (listaPalabras.size >= 2) {
                            hashMapOf("palabra" to listaPalabras[0], "traduccion" to listaPalabras[1])
                        } else {
                            hashMapOf("palabra" to "", "traduccion" to "")
                        }
                    }
                }
            }

            val data = hashMapOf(
                "palabras" to palabrasFirestore
            )

            // Actualiza el documento en Firestore
            usersCollection.document(emailEncriptado).update(data as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Palabras actualizadas correctamente")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error al actualizar las palabras", e)
                }
        }






    }
}