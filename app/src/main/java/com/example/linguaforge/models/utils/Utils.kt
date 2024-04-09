@file:Suppress("UNREACHABLE_CODE")

package com.example.linguaforge.models.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import kotlinx.coroutines.runBlocking

object Utils {
     val idiomas = arrayOf(
        "English", "Welsh", "Hindi", "Urdu", "Afrikaans", "Arabic",
        "Belarusian", "Bulgarian", "Bengali", "Catalan", "Czech", "Danish", "Dutch",
        "Finnish", "French", "German", "Greek", "Hungarian", "Italian", "Japanese",
        "Korean", "Norwegian", "Polish", "Portuguese", "Russian", "Spanish", "Swedish",
        "Turkish"
    )

    val idiomasConBanderas = mapOf(
        "English" to "🇬🇧",
        "Welsh" to "🇬🇧", // Welsh no tiene su propio emoji de bandera, así que utilizo el del Reino Unido
        "Hindi" to "🇮🇳",
        "Urdu" to "🇵🇰",
        "Afrikaans" to "🇿🇦",
        "Arabic" to "🇸🇦",
        "Belarusian" to "🇧🇾",
        "Bulgarian" to "🇧🇬",
        "Bengali" to "🇧🇩",
        "Catalan" to "🇪🇸", // Catalán se habla principalmente en España, pero no tiene su propia bandera emoji
        "Czech" to "🇨🇿",
        "Danish" to "🇩🇰",
        "Dutch" to "🇳🇱",
        "Finnish" to "🇫🇮",
        "French" to "🇫🇷",
        "German" to "🇩🇪",
        "Greek" to "🇬🇷",
        "Hungarian" to "🇭🇺",
        "Italian" to "🇮🇹",
        "Japanese" to "🇯🇵",
        "Korean" to "🇰🇷",
        "Norwegian" to "🇳🇴",
        "Polish" to "🇵🇱",
        "Portuguese" to "🇵🇹",
        "Russian" to "🇷🇺",
        "Spanish" to "🇪🇸",
        "Swedish" to "🇸🇪",
        "Turkish" to "🇹🇷"
    )

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

    fun getLanguageByCountryCode(countryCode: String): String {
        return when (countryCode) {
            "GB" -> "English" // Asumiendo Inglés por defecto para el Reino Unido
            "IN" -> "Hindi"
            "PK" -> "Urdu"
            "ZA" -> "Afrikaans"
            "SA" -> "Arabic"
            "BY" -> "Belarusian"
            "BG" -> "Bulgarian"
            "BD" -> "Bengali"
            "ES" -> "Spanish"
            "CZ" -> "Czech"
            "DK" -> "Danish"
            "NL" -> "Dutch"
            "FI" -> "Finnish"
            "FR" -> "French"
            "DE" -> "German"
            "GR" -> "Greek"
            "HU" -> "Hungarian"
            "IT" -> "Italian"
            "JP" -> "Japanese"
            "KR" -> "Korean"
            "NO" -> "Norwegian"
            "PL" -> "Polish"
            "PT" -> "Portuguese"
            "RU" -> "Russian"
            "SE" -> "Swedish"
            "TR" -> "Turkish"
            else -> "" // Manejar caso por defecto o desconocido
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

    fun anadirIdioma(titulo:String, subtitulo:String, idioma1: String, idioma2: String) = runBlocking {
        // Suponiendo que getIdiomas ahora devuelve List<Map<String, String>>?
        var idiomas: MutableList<Map<String, String>>? = UtilsDB.getIdiomas()?.toMutableList()
        if (idiomas == null) {
            idiomas = mutableListOf()
        }
        val nuevoIdioma = mapOf(
            "titulo" to titulo,
            "subtitulo" to subtitulo,
            "idiomaOrigen" to idioma1,
            "idiomaResultado" to idioma2
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

    fun eliminarPalabra(clave: String, position: Int) = runBlocking {
        // Obtiene la lista actual de palabras, que es un array de mapas
        val palabras: MutableList<Map<String, List<List<String>>>>? = UtilsDB.getPalabras()?.toMutableList()

        if (palabras != null) {
            // Encuentra el índice del mapa que corresponde a la clave proporcionada
            val indiceMapaEspecifico = palabras.indexOfFirst { it.containsKey(clave) }

            if (indiceMapaEspecifico != -1) {
                // Crea una copia mutable del mapa específico
                val mapaEspecificoMutable = palabras[indiceMapaEspecifico].toMutableMap()

                // Obtiene la lista de listas de palabras asociada a la clave y la convierte en mutable
                val listaDeListasPalabras = mapaEspecificoMutable[clave]?.toMutableList()

                if (listaDeListasPalabras != null) {
                    if (position == 999999 && listaDeListasPalabras.isNotEmpty()) {
                        // Elimina la última palabra del array
                        listaDeListasPalabras.removeAt(listaDeListasPalabras.size - 1)
                    } else if (position >= 0 && position < listaDeListasPalabras.size) {
                        // Elimina la palabra en la posición especificada
                        listaDeListasPalabras.removeAt(position)
                    } else {
                        // Maneja el caso en que la posición no sea válida
                        println("EliminarPalabra: Posición fuera de rango o lista de palabras nula")
                        return@runBlocking
                    }

                    // Actualiza el mapa mutable con la nueva lista
                    mapaEspecificoMutable[clave] = listaDeListasPalabras.toList()

                    // Reemplaza el mapa antiguo con el nuevo en el array de palabras
                    palabras[indiceMapaEspecifico] = mapaEspecificoMutable.toMap()

                    // Actualiza la base de datos con el array modificado
                    UtilsDB.setPalabras(palabras.toList())
                } else {
                    // Maneja el caso en que la lista de palabras sea nula
                    println("EliminarPalabra: Lista de palabras nula")
                }
            } else {
                // Maneja el caso en que la clave no se encuentra en el array
                println("EliminarPalabra: Clave no encontrada en el array de palabras")
            }
        } else {
            // Maneja el caso en que la lista de palabras sea nula
            println("EliminarPalabra: Lista de palabras nula")
        }
    }
    fun contarPalabrasPorClave(clave: String): Int = runBlocking {
        // Obtiene la lista actual de palabras, que es un array de mapas
        val palabras: MutableList<Map<String, List<List<String>>>>? = UtilsDB.getPalabras()?.toMutableList()

        if (palabras != null) {
            // Encuentra el mapa que corresponde a la clave proporcionada
            val mapaEspecifico = palabras.find { it.containsKey(clave) }

            if (mapaEspecifico != null) {
                // Obtiene la lista de listas de palabras asociada a la clave
                val listaDeListasPalabras = mapaEspecifico[clave]

                // Retorna el número de listas de palabras en la lista (cada lista representa una palabra)
                return@runBlocking listaDeListasPalabras?.size ?: 0
            } else {
                // Si la clave no se encuentra, retorna 0
                return@runBlocking 0
            }
        } else {
            // Si la lista de palabras es nula, retorna 0
            return@runBlocking 0
        }
    }

    fun existeTraduccion(palabraBuscada: String, traduccionBuscada: String): Boolean = runBlocking {
        val palabras = UtilsDB.getPalabras() // Obtiene la lista de palabras que es un array de mapas

        palabras?.forEach { mapaIdioma ->
            // Itera a través de cada entrada del mapa de idioma
            mapaIdioma.forEach { (_, listaDePalabras) ->
                // Verifica si alguna de las listas de palabras contiene la palabra y traducción buscada
                listaDePalabras.any { lista ->
                    lista.size >= 2 && lista[0] == palabraBuscada && lista[1] == traduccionBuscada
                }.let { existe ->
                    if (existe) return@runBlocking true // Retorna true si encuentra una coincidencia
                    println("La palabra ya existe")
                }
            }
        }
        println("La palabra no existe")
        return@runBlocking false // Retorna false si no se encontró ninguna coincidencia

    }







}





