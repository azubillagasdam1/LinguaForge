package com.example.linguaforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaforge.models.utils.Utils

class MyPalabraAdapter(private val itemPalabra: List<ItemPalabra>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MyPalabraAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val original: TextView = view.findViewById(R.id.originalTextView)
        val traduccion: TextView = view.findViewById(R.id.traduccionTextView)


        fun bind(ItemPalabra: ItemPalabra, listener: MyPalabraAdapter.OnItemClickListener, position: Int) {
            original.text = ItemPalabra.original
            traduccion.text = ItemPalabra.traduccion
            itemView.setOnClickListener { listener.onItemClick(position) }

            // Establece un OnLongClickListener para detectar cuando se mantiene presionado
            itemView.setOnLongClickListener {
                listener.onItemLongClick(position)
                true // Indica que el evento ha sido manejado
            }
        }
        interface OnItemClickListener {
            fun onItemClick(position: Int)
            fun onItemLongClick(position: Int) // Método para manejar la presión prolongada
        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.palabra_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemPalabra[position]
        holder.bind(item, listener, position)
    }

    override fun getItemCount() = itemPalabra.size


}


data class ItemPalabra(val idioma: String, val original: String, val traduccion: String)
