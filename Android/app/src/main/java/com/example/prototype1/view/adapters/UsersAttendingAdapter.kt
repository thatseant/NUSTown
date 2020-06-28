package com.example.prototype1.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prototype1.R
import com.example.prototype1.model.NUser
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView

class UsersAttendingAdapter(mListener: OnItemSelectedListener) : ListAdapter<NUser, UsersAttendingAdapter.ViewHolder>(NUserDiffCallback()) {
    private val newListener: OnItemSelectedListener = mListener


    interface OnItemSelectedListener {
        fun onItemSelected(mUser: NUser, view: View)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//Called for every item in RecyclerView when it becomes visible
        val item = getItem(position)
        holder.bind(item, holder, newListener) //bind function in ViewHolder sets Views within it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userImage: CircularImageView = itemView.findViewById(R.id.userPic)

        fun bind(item: NUser, holder: ViewHolder, listener: OnItemSelectedListener) {
            //Sets ImageView
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("profile/" + item.profilePic)

            imageRef.downloadUrl.addOnSuccessListener {
                Glide.with(holder.userImage.context).load(it).into(userImage)
            }

//            //Sets ImageView
//            if (item.profilePic != "") {
//                Glide.with(holder.userImage.context).load(item.profilePic).apply(RequestOptions()
//                        .placeholder(R.drawable.nus)
//                ).thumbnail(0.02f).into(eventImage)
//            } else {
//                eventImage.setImageResource(R.drawable.nus)
//            }


            itemView.setOnClickListener { view ->
                listener.onItemSelected(item, view)
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
    class NUserDiffCallback : DiffUtil.ItemCallback<NUser>() {

        override fun areItemsTheSame(oldItem: NUser, newItem: NUser): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: NUser, newItem: NUser): Boolean {
            return oldItem == newItem
        }


    }


}
