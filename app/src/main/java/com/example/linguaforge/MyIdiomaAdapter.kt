package com.example.linguaforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaforge.models.utils.Utils

class MyIdiomaAdapter(private val itemIdiomas: List<ItemIdioma>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MyIdiomaAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleTextView)
        val subtitle: TextView = view.findViewById(R.id.subtitleTextView)
        val flag: TextView = view.findViewById(R.id.flagTextView)

        fun bind(itemIdioma: ItemIdioma, listener: OnItemClickListener, position: Int) {
            title.text = itemIdioma.title
            subtitle.text = itemIdioma.subtitle
            println("idiomaAdapter1:" +  itemIdioma.idioma1)
            println("idiomaAdapter2:" +  itemIdioma.idioma2)
            flag.text = Utils.getFlagEmoji(Utils.getCountryCode(itemIdioma.idioma2))
            itemView.setOnClickListener { listener.onItemClick(position) }
            itemView.setOnLongClickListener {
                listener.onItemLongClick(position)
                true // Indica que el evento ha sido manejado
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.idioma_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemIdiomas[position]
        holder.bind(item, listener, position)
    }

    override fun getItemCount() = itemIdiomas.size
}

data class ItemIdioma(val title: String, val subtitle: String, val idioma1: String, val idioma2: String)
