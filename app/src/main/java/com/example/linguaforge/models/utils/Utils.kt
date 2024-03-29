@file:Suppress("UNREACHABLE_CODE")

package com.example.linguaforge.models.utils

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage

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
        }





