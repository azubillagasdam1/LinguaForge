package com.example.linguaforge.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.linguaforge.R
import com.example.linguaforge.models.utils.Utils


class CrearIdiomaFragment(val contextoElegirActivity:Context) : DialogFragment() {

    private val idiomas = arrayOf(
        "English", "Welsh", "Hindi", "Urdu", "Afrikaans", "Arabic",
        "Belarusian", "Bulgarian", "Bengali", "Catalan", "Czech", "Danish", "Dutch",
        "Finnish", "French", "German", "Greek", "Hungarian", "Italian", "Japanese",
        "Korean", "Norwegian", "Polish", "Portuguese", "Russian", "Spanish", "Swedish",
        "Turkish"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crear_idioma, container, false)
        val spinnerIdioma = view.findViewById<Spinner>(R.id.spinnerIdioma)
        val tituloEditText = view.findViewById<EditText>(R.id.tituloEditText)
        val subtituloEditText = view.findViewById<EditText>(R.id.subtituloEditText)
        val banderaTextView = view.findViewById<TextView>(R.id.banderaTextView) // Encuentra el TextView
        val crearButton = view.findViewById<Button>(R.id.crearButton) // Encuentra el botón de crear
        var idiomaSeleccionado:String? = null
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, idiomas)
        spinnerIdioma.adapter = adapter
        // Establecer el listener para el spinner
        spinnerIdioma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                 idiomaSeleccionado = idiomas[position]  // Obtiene el idioma seleccionado
                // Obtén el código del país para el idioma y luego el emoji de la bandera
                val countryCode = Utils.getCountryCode(idiomaSeleccionado!!)
                val flagEmoji = Utils.getFlagEmoji(countryCode)
                banderaTextView.text = flagEmoji // Actualiza el texto del TextView con el emoji
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Opcional: Manejar cualquier caso donde no se selecciona ningún elemento
            }
        }

        crearButton.setOnClickListener {
            Utils.anadirIdioma(tituloEditText.text.toString(),subtituloEditText.text.toString(),idiomaSeleccionado!!)
            Utils.anadirPalabra("enge-es","hello","hola")
            Utils.recargarActividad(contextoElegirActivity)
            closeFragment()
        }
        return view
    }






    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = resources.getDimensionPixelSize(R.dimen.idioma_dialog_fragment_height)
            setLayout(width, height)
        }
    }

    fun closeFragment() {
        dismiss()

    }
}
