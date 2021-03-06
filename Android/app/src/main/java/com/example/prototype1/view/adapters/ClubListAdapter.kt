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

class ClubListAdapter(mListener: OnItemSelectedListener) : ListAdapter<NClub, ClubListAdapter.ViewHolder>(NClubDiffCallback()) {
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
            if (item.imgUrl != "") {
                Glide.with(holder.clubImage.context).load(item.imgUrl).apply(RequestOptions()
                        .placeholder(R.drawable.ic_baseline_emoji_people_50)
                ).thumbnail(0.02f).into(clubImage)
            } else {
                clubImage.setImageResource(R.drawable.ic_baseline_emoji_people_50)
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
class NClubDiffCallback : DiffUtil.ItemCallback<NClub>() {

    override fun areItemsTheSame(oldItem: NClub, newItem: NClub): Boolean {
        return oldItem.ID == newItem.ID
    }

    override fun areContentsTheSame(oldItem: NClub, newItem: NClub): Boolean {
        return oldItem == newItem
    }


}
