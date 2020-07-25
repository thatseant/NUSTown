package com.example.prototype1.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.prototype1.R
import com.example.prototype1.model.NEvent
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class ClubEventsAdapter(mListener: OnItemSelectedListener) : ListAdapter<NEvent, ClubEventsAdapter.ViewHolder>(NEventDiffCallback()) {
    private val newListener: OnItemSelectedListener = mListener

    interface OnItemSelectedListener {
        fun onItemSelected(mEvent: NEvent, view: View)
        fun onJioSelected(mEvent: NEvent, view: View)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//Called for every item in RecyclerView when it becomes visible
        val item = getItem(position)
        holder.bind(item, holder, newListener) //bind function in ViewHolder sets Views within it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventCCA: TextView = itemView.findViewById(R.id.titleCell)
        private val eventDate: TextView = itemView.findViewById(R.id.statusCell)
        private val eventImage: ImageView = itemView.findViewById(R.id.imageCell)

        fun bind(item: NEvent, holder: ViewHolder, listener: OnItemSelectedListener) {
            eventCCA.text = item.name
            val dateFormat = SimpleDateFormat("EEE dd/MM HH:mm", Locale.ENGLISH)
            eventDate.text = dateFormat.format(item.time)

            //Sets ImageView
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("events/" + item.ID + ".png")

            imageRef.downloadUrl.addOnSuccessListener {
                Glide.with(holder.eventImage.context).load(it).apply(RequestOptions().centerCrop()
                        .placeholder(R.drawable.ic_baseline_event_50)).thumbnail(0.02f).into(eventImage)

            }.addOnFailureListener { eventImage.setImageResource(R.drawable.ic_baseline_event_50); }

            itemView.setOnClickListener { view ->
                if (item.ID.contains("_") || item.ID.contains(" ")) { //TODO: Temporary Solution
                    listener.onItemSelected(item, view)
                } else {
                    listener.onJioSelected(item, view)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.club_event_cell, parent, false)

                return ViewHolder(view)
            }
        }
    }

}
