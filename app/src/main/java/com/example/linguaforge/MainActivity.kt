package com.example.linguaforge

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION_CODE = 1
    private var fromLanguageCode = 0
    private var toLanguageCode = 0

    private var fromLanguages = arrayOf(
        "From", "English", "Welsh", "Hindi", "Urdu", "Afrikaans", "Arabic",
        "Belarusian", "Bulgarian", "Bengali", "Catalan", "Czech", "Danish", "Dutch",
        "Finnish", "French", "German", "Greek", "Hungarian", "Italian", "Japanese",
        "Korean", "Norwegian", "Polish", "Portuguese", "Russian", "Spanish", "Swedish",
        "Turkish"
    )

    private var toLanguages = fromLanguages // Assuming toLanguages should be the same as fromLanguages

    private var fromSpinner: Spinner? = null
    private var toSpinner: Spinner? = null
    private var sourceText: TextInputEditText? = null
    private var micIV: ImageView? = null
    private var translateBtn: MaterialButton? = null
    private var translateTV: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fromSpinner = findViewById(R.id.idFromSpinner)
        toSpinner = findViewById(R.id.idToSpinner)
        sourceText = findViewById(R.id.idEditSource)
        micIV = findViewById(R.id.idIVMic)
        translateBtn = findViewById(R.id.idBtnTranslation)
        translateTV = findViewById(R.id.idTranslatedTV)

        setupSpinners()
    }

    private fun setupSpinners() {
        val fromAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fromLanguages)
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromSpinner?.adapter = fromAdapter

        val toAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, toLanguages)
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toSpinner?.adapter = toAdapter

        fromSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
               toLanguageCode = getLanguageCode(toLanguages[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional implementation
            }
        }
    }



    private fun getLanguageCode(language: String): Int {
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
}
