package com.example.prototype1.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.prototype1.R
import com.example.prototype1.model.NEvent
import com.example.prototype1.model.NMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : ListAdapter<NMessage, ChatAdapter.ViewHolder>(NMessageDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//Called for every item in RecyclerView when it becomes visible
        val item = getItem(position)
        holder.bind(item, holder) //bind function in ViewHolder sets Views within it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receivedBubble: LinearLayout = itemView.findViewById(R.id.bubble_chat_received)
        private val sentBubble: LinearLayout = itemView.findViewById(R.id.bubble_chat_sent)
        private val receivedText: TextView = itemView.findViewById(R.id.textview_chat_received)
        private val sentText: TextView = itemView.findViewById(R.id.textview_chat_sent)
        private val userPic: CircularImageView = itemView.findViewById(R.id.userPic)
        private val username_sent: TextView = itemView.findViewById(R.id.username_sent)
        private val username_received: TextView = itemView.findViewById(R.id.username_received)

        fun bind(chatMessage: NMessage, holder: ViewHolder) {

            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            //Sets ImageView
            val storageReference = FirebaseStorage.getInstance().reference
            val username = chatMessage.user
            val imageRef = storageReference.child("profile/$username.jpg")

            imageRef.downloadUrl.addOnSuccessListener {
                Glide.with(holder.userPic.context).load(it).into(userPic)
            }

            if (chatMessage.user == userEmail) { //TODO: Change to current user
                sentText.text = chatMessage.text
                receivedBubble.visibility = View.GONE
                username_sent.text = chatMessage.user
            } else {
                receivedText.text = chatMessage.text
                sentBubble.visibility = View.GONE
                username_received.text = chatMessage.user
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.bubble_chat, parent, false)

                return ViewHolder(view)
            }
        }
    }

    //Only refreshes items that changed in RecyclerView
    class NMessageDiffCallback : DiffUtil.ItemCallback<NMessage>() {

        override fun areItemsTheSame(oldItem: NMessage, newItem: NMessage): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: NMessage, newItem: NMessage): Boolean {
            return oldItem == newItem
        }


    }

}
