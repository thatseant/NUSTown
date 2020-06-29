package com.example.prototype1.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prototype1.R
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView

class UsersAttendingAdapter : ListAdapter<String, UsersAttendingAdapter.ViewHolder>(StringDiffCallback()) {

    interface OnItemSelectedListener {
        fun onItemSelected(mUser: String, view: View)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//Called for every item in RecyclerView when it becomes visible
        val item = getItem(position)
        holder.bind(item, holder) //bind function in ViewHolder sets Views within it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userImage: CircularImageView = itemView.findViewById(R.id.userPic)

        fun bind(item: String, holder: ViewHolder) {
            //Sets ImageView
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("profile/" + item + ".jpg")

            imageRef.downloadUrl.addOnSuccessListener {
                Glide.with(holder.userImage.context).load(it).into(userImage)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.attendees_cell, parent, false)

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
