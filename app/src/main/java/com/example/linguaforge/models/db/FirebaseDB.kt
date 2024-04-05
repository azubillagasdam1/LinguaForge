package com.example.linguaforge.models.db

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


/**
 * Clase de utilidad para obtener instancias de Firebase Firestore, Auth y Storage.
 * Utiliza el patrón Singleton para garantizar una única instancia de cada servicio.
 */
class FirebaseDB {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCESTORE: FirebaseFirestore? = null
        private var INSTANCEAUTH: FirebaseAuth? = null

        /**
         * Obtiene y devuelve la instancia única de Firebase Firestore.
         *
         * @return Instancia de FirebaseFirestore.
         */
        fun getInstanceFirestore(): FirebaseFirestore {
            synchronized(this) {
                if (INSTANCESTORE == null)
                    INSTANCESTORE = FirebaseFirestore.getInstance()
                return INSTANCESTORE as FirebaseFirestore
            }
        }

        /**
         * Obtiene y devuelve la instancia única de FirebaseAuth.
         *
         * @return Instancia de FirebaseAuth.
         */
        fun getInstanceFirebase(): FirebaseAuth {
            synchronized(this) {
                if (INSTANCEAUTH == null)
                    INSTANCEAUTH = FirebaseAuth.getInstance()
                return INSTANCEAUTH as FirebaseAuth
            }
        }

        /**
         * Obtiene y devuelve la instancia única de FirebaseStorage.
         *
         * @return Instancia de FirebaseStorage.
         */

        fun signOut() {
            getInstanceFirebase().signOut()
        }
    }
}