package com.example.prototype1.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prototype1.R
import com.example.prototype1.model.NEvent
import java.text.SimpleDateFormat
import java.util.*

class JioListAdapter(mListener: OnItemSelectedListener) : ListAdapter<NEvent, JioListAdapter.ViewHolder>(NEventDiffCallback()) {
    private val newListener: OnItemSelectedListener = mListener


    interface OnItemSelectedListener {
        fun onItemSelected(mEvent: NEvent, view: View)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//Called for every item in RecyclerView when it becomes visible
        val item = getItem(position)
        holder.bind(item, holder, newListener) //bind function in ViewHolder sets Views within it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val jioTitle: TextView = itemView.findViewById(R.id.jioTitleCell)
        private val jioDate: TextView = itemView.findViewById(R.id.jioStatusCell)
        private val jioNumber: TextView = itemView.findViewById(R.id.jioNumberAttending)

        fun bind(item: NEvent, holder: ViewHolder, listener: OnItemSelectedListener) {
            jioTitle.text = item.name
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
            jioDate.text = dateFormat.format(item.time)
            if (item.maxAttending == 0) {
                jioNumber.text = item.numberAttending.toString() + " Attending"
            } else {
                jioNumber.text = item.numberAttending.toString() + "/" + item.maxAttending.toString() + " Attending"
            }


            itemView.setOnClickListener { view ->
                listener.onItemSelected(item, view)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.jio_cell, parent, false)

                return ViewHolder(view)
            }
        }
    }

}
