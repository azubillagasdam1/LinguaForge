package com.example.linguaforge.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.example.linguaforge.R
import com.example.linguaforge.activitys.PalabrasActivity
import com.example.linguaforge.models.utils.Utils

class EditarPalabraFragment(
    var contextoPalabrasActivity: Context,
    val position: Int,
    val clave: String,
    val original: String,
    val traduccion: String
) : DialogFragment() {

    private lateinit var editTextOriginal: EditText
    private lateinit var editTextTraduccion: EditText
    private lateinit var infoTextView: TextView
    private lateinit var guardarButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.editar_palabra_fragment, container, false)

        // Configurar el ImageView para cerrar el fragmento
        val imageViewCerrar = view.findViewById<ImageView>(R.id.imageViewCerrar)
        imageViewCerrar.setOnClickListener {
            // Cierra el fragmento
            dismiss()
        }

        // Inicializar los EditText y el botón
        editTextOriginal = view.findViewById(R.id.editTextOriginal)
        editTextTraduccion = view.findViewById(R.id.editTextTraduccion)
        guardarButton = view.findViewById(R.id.guardarButton)
        infoTextView = view.findViewById(R.id.infoTextView)

        inicializarEditTexts()

        // Configurar el listener del botón
        guardarButton.setOnClickListener {

            val textoOriginal = editTextOriginal.text.toString()
            val textoTraduccion = editTextTraduccion.text.toString()
            if (Utils.existeTraduccion(textoOriginal, textoTraduccion)) {
                infoTextView.text = "La palabra ya existe."
                infoTextView.visibility = View.VISIBLE
            } else {
                Utils.eliminarPalabra(clave, position)
                Utils.anadirPalabraEnPosicion(clave, textoOriginal, textoTraduccion, position)
                infoTextView.visibility = View.GONE
                llamarRecargar(contextoPalabrasActivity)
                closeFragment()

            }

        }

        return view
    }
    fun llamarRecargar(contexto: Context) {
        (contexto as? PalabrasActivity)?.recargar()
    }
    private fun inicializarEditTexts() {
        // Asignar los valores por defecto a los EditText
        editTextOriginal.setText(original)
        editTextTraduccion.setText(traduccion)
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
