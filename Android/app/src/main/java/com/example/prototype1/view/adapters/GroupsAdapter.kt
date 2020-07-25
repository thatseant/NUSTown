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
import com.example.prototype1.model.NClub
import com.google.firebase.storage.FirebaseStorage

class GroupsAdapter(mListener: OnItemSelectedListener) : ListAdapter<NClub, GroupsAdapter.ViewHolder>(NClubDiffCallback()) {
    private val newListener: OnItemSelectedListener = mListener


    interface OnItemSelectedListener {
        fun onItemSelected(mClub: NClub, view: View)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//Called for every item in RecyclerView when it becomes visible
        val item = getItem(position)
        holder.bind(item, holder, newListener) //bind function in ViewHolder sets Views within it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clubName: TextView = itemView.findViewById(R.id.titleCell)
        private val clubCat: TextView = itemView.findViewById(R.id.statusCell)
        private val clubImage: ImageView = itemView.findViewById(R.id.imageCell)

        fun bind(item: NClub, holder: ViewHolder, listener: OnItemSelectedListener) {
            clubName.text = item.name
            clubCat.visibility = View.GONE

            //Sets ImageView

            //Sets ImageView
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("groups/" + item.name + ".jpg")

            imageRef.downloadUrl.addOnSuccessListener {
                Glide.with(holder.clubImage.context).load(it).apply(RequestOptions().centerCrop()
                        .placeholder(R.drawable.ic_baseline_deck_50)).thumbnail(0.02f).into(clubImage)

            }.addOnFailureListener { clubImage.setImageResource(R.drawable.ic_baseline_deck_50) }

            itemView.setOnClickListener { view ->
                listener.onItemSelected(item, view)
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