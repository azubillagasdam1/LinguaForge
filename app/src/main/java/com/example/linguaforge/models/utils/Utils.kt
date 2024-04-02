@file:Suppress("UNREACHABLE_CODE")

package com.example.linguaforge.models.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import kotlinx.coroutines.runBlocking

object Utils {

    fun getFlagEmoji(countryCode: String): String {
        if (countryCode.isEmpty() || countryCode.length != 2) {
            return "Código de país inválido"
        }

        // Convertimos el código del país a mayúsculas para estandarizar la entrada.
        val upperCaseCountryCode = countryCode.uppercase()

        // Construimos el emoji de la bandera.
        val firstLetter = upperCaseCountryCode[0].code - 'A'.code + 0x1F1E6
        val secondLetter = upperCaseCountryCode[1].code - 'A'.code + 0x1F1E6

        // Retornamos la concatenación de los caracteres correspondientes a las letras del código del país.
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    fun getCountryCode(language: String): String {
        return when (language) {
            "English" -> "GB" // Reino Unido
            "Welsh" -> "GB" // Gales, parte del Reino Unido
            "Hindi" -> "IN" // India
            "Urdu" -> "PK" // Pakistán
            "Afrikaans" -> "ZA" // Sudáfrica
            "Arabic" -> "SA" // Arabia Saudita
            "Belarusian" -> "BY" // Bielorrusia
            "Bulgarian" -> "BG" // Bulgaria
            "Bengali" -> "BD" // Bangladés
            "Catalan" -> "ES" // España, específicamente Cataluña
            "Czech" -> "CZ" // República Checa
            "Danish" -> "DK" // Dinamarca
            "Dutch" -> "NL" // Países Bajos
            "Finnish" -> "FI" // Finlandia
            "French" -> "FR" // Francia
            "German" -> "DE" // Alemania
            "Greek" -> "GR" // Grecia
            "Hungarian" -> "HU" // Hungría
            "Italian" -> "IT" // Italia
            "Japanese" -> "JP" // Japón
            "Korean" -> "KR" // Corea del Sur
            "Norwegian" -> "NO" // Noruega
            "Polish" -> "PL" // Polonia
            "Portuguese" -> "PT" // Portugal
            "Russian" -> "RU" // Rusia
            "Spanish" -> "ES" // España
            "Swedish" -> "SE" // Suecia
            "Turkish" -> "TR" // Turquía
            else -> "" // Manejar caso por defecto o desconocido
        }
    }

    fun getLanguageFirebaseCode(language: String): Int {
        return when (language) {
            "English" -> FirebaseTranslateLanguage.EN
            "Welsh" -> FirebaseTranslateLanguage.CY
            "Hindi" -> FirebaseTranslateLanguage.HI
            "Urdu" -> FirebaseTranslateLanguage.UR
            "Afrikaans" -> FirebaseTranslateLanguage.AF
            "Arabic" -> FirebaseTranslateLanguage.AR
            "Belarusian" -> FirebaseTranslateLanguage.BE
            "Bulgarian" -> FirebaseTranslateLanguage.BG
            "Bengali" -> FirebaseTranslateLanguage.BN
            "Catalan" -> FirebaseTranslateLanguage.CA
            "Czech" -> FirebaseTranslateLanguage.CS
            "Danish" -> FirebaseTranslateLanguage.DA
            "Dutch" -> FirebaseTranslateLanguage.NL
            "Finnish" -> FirebaseTranslateLanguage.FI
            "French" -> FirebaseTranslateLanguage.FR
            "German" -> FirebaseTranslateLanguage.DE
            "Greek" -> FirebaseTranslateLanguage.EL
            "Hungarian" -> FirebaseTranslateLanguage.HU
            "Italian" -> FirebaseTranslateLanguage.IT
            "Japanese" -> FirebaseTranslateLanguage.JA
            "Korean" -> FirebaseTranslateLanguage.KO
            "Norwegian" -> FirebaseTranslateLanguage.NO
            "Polish" -> FirebaseTranslateLanguage.PL
            "Portuguese" -> FirebaseTranslateLanguage.PT
            "Russian" -> FirebaseTranslateLanguage.RU
            "Spanish" -> FirebaseTranslateLanguage.ES
            "Swedish" -> FirebaseTranslateLanguage.SV
            "Turkish" -> FirebaseTranslateLanguage.TR
            else -> FirebaseTranslateLanguage.EN // Default or consider throwing an error
        }
    }
     fun recargarActividad(context: Context) {
         if (context is AppCompatActivity) {
             (context as AppCompatActivity).recreate()
         }
     }
    fun salirAplicacion(activity: Activity){
        activity.finishAffinity()
    }

    fun anadirIdioma(titulo:String, subtitulo:String, idioma: String) = runBlocking {
        // Suponiendo que getIdiomas ahora devuelve List<Map<String, String>>?
        var idiomas: MutableList<Map<String, String>>? = UtilsDB.getIdiomas()?.toMutableList()
        if (idiomas == null) {
            idiomas = mutableListOf()
        }
        val nuevoIdioma = mapOf(
            "titulo" to titulo,
            "subtitulo" to subtitulo,
            "idioma" to idioma
        )
        idiomas.add(nuevoIdioma)
        UtilsDB.setIdioma(idiomas)

    }
    fun eliminarIdioma(indice: Int) = runBlocking {
        // Obtiene la lista actual de idiomas
        var idiomas: MutableList<Map<String, String>>? = UtilsDB.getIdiomas()?.toMutableList()
        if (idiomas != null && indice >= 0 && indice < idiomas.size) {
            // Elimina el idioma en el índice especificado
            idiomas.removeAt(indice)

            // Actualiza la lista de idiomas en la base de datos
            UtilsDB.setIdioma(idiomas)
        } else {
            // Puedes manejar el caso en que el índice no sea válido o la lista sea nula
            Log.e("EliminarIdioma", "Índice fuera de rango o lista de idiomas nula")
        }
    }

    fun anadirPalabra( idioma: String,palabra: String, traduccion: String) = runBlocking {
        var idiomas: MutableList<Map<String, List<List<String>>>>? = UtilsDB.getPalabras()?.toMutableList()
        if (idiomas == null) {
            idiomas = mutableListOf()
        }
        var mapaIdioma = idiomas.find { it.containsKey(idioma) }

        if (mapaIdioma == null) {
            mapaIdioma = mapOf(idioma to mutableListOf(listOf(palabra, traduccion)))
            idiomas.add(mapaIdioma)
        } else {
            val traducciones = mapaIdioma[idioma]?.toMutableList() ?: mutableListOf()
            traducciones.add(listOf(palabra, traduccion))
            mapaIdioma = mapOf(idioma to traducciones)
            idiomas[idiomas.indexOfFirst { it.containsKey(idioma) }] = mapaIdioma
        }

        UtilsDB.setPalabra(idiomas)

    }


}





