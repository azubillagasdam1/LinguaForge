package com.example.linguaforge.models.utils

object Utils {

    fun getFlagEmoji(countryCode: String): String {
        // Convertimos el código del país a mayúsculas para estandarizar la entrada.
        val upperCaseCountryCode = countryCode.uppercase()

        // Verificamos que el código del país tiene exactamente 2 caracteres.
        if (upperCaseCountryCode.length != 2) {
            return "Código de país inválido"
        }

        // Construimos el emoji de la bandera.
        val firstLetter = upperCaseCountryCode[0].code - 'A'.code + 0x1F1E6
        val secondLetter = upperCaseCountryCode[1].code - 'A'.code + 0x1F1E6

        // Retornamos la concatenación de los caracteres correspondientes a las letras del código del país.
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }
}
