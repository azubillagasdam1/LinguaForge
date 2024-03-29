package com.example.linguaforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaforge.models.utils.Utils

class MyAdapter(private val items: List<Item>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleTextView)
        val subtitle: TextView = view.findViewById(R.id.subtitleTextView)
        val flag: TextView = view.findViewById(R.id.flagTextView)

        fun bind(item: Item, listener: MyAdapter.OnItemClickListener, position: Int) {
            title.text = item.title
            subtitle.text = item.subtitle
            flag.text = Utils.getFlagEmoji(Utils.getCountryCode(item.country))
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
            .inflate(R.layout.idioma_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, listener, position)
    }

    override fun getItemCount() = items.size


}


data class Item(val title: String, val subtitle: String, val country: String)
