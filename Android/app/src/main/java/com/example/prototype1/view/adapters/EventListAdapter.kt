package com.example.prototype1.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.prototype1.R
import com.example.prototype1.model.NEvent

class EventListAdapter(mListener: OnItemSelectedListener) : ListAdapter<NEvent, EventListAdapter.ViewHolder>(NEventDiffCallback()) {
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
        private val eventCCA: TextView = itemView.findViewById(R.id.titleCell)
        private val eventDate: TextView = itemView.findViewById(R.id.statusCell)
        private val eventImage: ImageView = itemView.findViewById(R.id.imageCell)

        fun bind(item: NEvent, holder: ViewHolder, listener: OnItemSelectedListener) {
            eventCCA.text = item.name
            eventDate.text = item.time.toString()

            //Sets ImageView
//            val storageReference = FirebaseStorage.getInstance().reference
//            val imageRef = storageReference.child("events/" + item.image)
//
//            imageRef.downloadUrl.addOnSuccessListener {
//                Glide.with(holder.eventImage.context).load(it).apply(RequestOptions()
//                        .placeholder(R.drawable.nus)).thumbnail(0.02f).into(eventImage)
//
//            }

            //Sets ImageView
            if (item.imgUrl != "") {
                Glide.with(holder.eventImage.context).load(item.imgUrl).apply(RequestOptions()
                        .placeholder(R.drawable.nus)
                ).thumbnail(0.02f).into(eventImage)
            } else {
                eventImage.setImageResource(R.drawable.nus)
            }


            itemView.setOnClickListener { view ->
                listener.onItemSelected(item, view)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.event_cell, parent, false)

                return ViewHolder(view)
            }
        }
    }

}

//Only refreshes items that changed in RecyclerView
class NEventDiffCallback : DiffUtil.ItemCallback<NEvent>() {

    override fun areItemsTheSame(oldItem: NEvent, newItem: NEvent): Boolean {
        return oldItem.ID == newItem.ID
    }

    override fun areContentsTheSame(oldItem: NEvent, newItem: NEvent): Boolean {
        return oldItem == newItem
    }


}
