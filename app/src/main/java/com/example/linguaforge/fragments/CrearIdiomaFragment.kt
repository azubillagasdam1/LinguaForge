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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crear_idioma, container, false)
        val spinnerIdioma1 = view.findViewById<Spinner>(R.id.idioma1Spinner)
        val spinnerIdioma2 = view.findViewById<Spinner>(R.id.spinnerIdioma)
        val tituloEditText = view.findViewById<EditText>(R.id.tituloEditText)
        val subtituloEditText = view.findViewById<EditText>(R.id.subtituloEditText)
        val banderaTextView = view.findViewById<TextView>(R.id.banderaTextView) // Encuentra el TextView
        val crearButton = view.findViewById<Button>(R.id.crearButton) // Encuentra el botón de crear
        var idioma1Seleccionado:String? = null
        var idioma2Seleccionado:String? = null


        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Utils.idiomasConBanderas.values.toList())
        spinnerIdioma1.adapter = adapter1
// Establecer el listener para el spinner
        spinnerIdioma1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Aquí obtenemos el nombre del idioma basado en la posición del emoji seleccionado
                idioma1Seleccionado = Utils.idiomasConBanderas.keys.toList()[position]
                // Aunque mostramos emojis en el spinner, guardamos el nombre del idioma correspondiente
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Opcional: Manejar cualquier caso donde no se selecciona ningún elemento
            }
        }






        val adapter2 =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Utils.idiomas)
        spinnerIdioma2.adapter = adapter2
        // Establecer el listener para el spinner
        spinnerIdioma2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                 idioma2Seleccionado = Utils.idiomas[position]  // Obtiene el idioma seleccionado
                // Obtén el código del país para el idioma y luego el emoji de la bandera
                val countryCode = Utils.getCountryCode(idioma2Seleccionado!!)
                val flagEmoji = Utils.getFlagEmoji(countryCode)
                banderaTextView.text = flagEmoji // Actualiza el texto del TextView con el emoji
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Opcional: Manejar cualquier caso donde no se selecciona ningún elemento
            }
        }

        crearButton.setOnClickListener {
            Utils.anadirIdioma(tituloEditText.text.toString(),subtituloEditText.text.toString(),idioma1Seleccionado!!,idioma2Seleccionado!!)
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
