package com.example.linguaforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaforge.models.utils.Utils

class MyAdapter(private val items: List<Item>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleTextView)
        val subtitle: TextView = view.findViewById(R.id.subtitleTextView)
        val flag: TextView = view.findViewById(R.id.flagTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.idioma_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.flag.text = Utils.getFlagEmoji("ES")

    }

    override fun getItemCount() = items.size
}

data class Item(val title: String, val subtitle: String)
