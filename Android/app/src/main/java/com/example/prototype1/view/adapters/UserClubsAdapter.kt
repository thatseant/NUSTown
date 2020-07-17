package com.example.prototype1.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prototype1.R

class UserClubsAdapter(mListener: ClickListener) : ListAdapter<String, UserClubsAdapter.ViewHolder>(StringDiffCallback()) {
    private val newListener: ClickListener = mListener

    interface ClickListener {
        fun onUnfollow(clubName: String)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//Called for every item in RecyclerView when it becomes visible
        val item = getItem(position)
        holder.bind(item, holder, newListener) //bind function in ViewHolder sets Views within it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clubName: TextView = itemView.findViewById(R.id.club_name)
        private val unfollowButton: Button = itemView.findViewById(R.id.unfollow_button)

        fun bind(item: String, holder: ViewHolder, listener: ClickListener) {

            clubName.text = item

            unfollowButton.setOnClickListener { v ->
                listener.onUnfollow(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.following_cell, parent, false)

                return ViewHolder(view)
            }
        }
    }

    //Only refreshes items that changed in RecyclerView
    class StringDiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }


    }


}