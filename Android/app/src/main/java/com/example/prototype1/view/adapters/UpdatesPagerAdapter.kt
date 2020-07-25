package com.example.prototype1.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prototype1.R
import com.google.firebase.storage.FirebaseStorage

class UpdatesPagerAdapter(mListener: OnItemSelectedListener) : ListAdapter<Map.Entry<String, ArrayList<String>>, UpdatesPagerAdapter.ViewHolder>(UpdatesDiffCallback()) {
    private val newListener: OnItemSelectedListener = mListener


    interface OnItemSelectedListener {
        fun onItemSelected(MPost: Map.Entry<String, ArrayList<String>>, view: View)
        fun deleteItemSelected(MPost: Map.Entry<String, ArrayList<String>>, view: View)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//Called for every item in RecyclerView when it becomes visible
        val item = getItem(position)
        holder.bind(position, item, holder, newListener) //bind function in ViewHolder sets Views within it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        private val postDate: TextView = itemView.findViewById(R.id.postTitle)
        private val postCaption: TextView = itemView.findViewById(R.id.postStatus)
        private val editButton: Button = itemView.findViewById(R.id.edit_post_button)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_post_button)
        private val postImage: ImageView = itemView.findViewById(R.id.postImage)

        fun bind(position: Int, item: Map.Entry<String, ArrayList<String>>, holder: ViewHolder, listener: OnItemSelectedListener) {
            var cleaned_text = item.value[0].replace("&([a-z0-9]+|#[0-9]{1,6}|#x[0-9a-f]{1,6});".toRegex(), " ") //Remove &...; html
            postCaption.text = cleaned_text
//            editButton.visibility = VISIBLE //TODO: Organisers only
            editButton.setOnClickListener { view ->
                listener.onItemSelected(item, view)
            }
//            deleteButton.visibility = VISIBLE
            deleteButton.setOnClickListener { view ->
                listener.deleteItemSelected(item, view)
            }

            if (item.value.size>2 && position!=0) {
                val storageReference = FirebaseStorage.getInstance().reference
                val imageRef = storageReference.child("updates/" + item.value[2] + ".png")

                imageRef.downloadUrl.addOnSuccessListener {
                    Glide.with(holder.postImage.context).load(it).into(postImage)
                }
            } else if (position == 0) {
                postImage.visibility = View.GONE
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.post_cell, parent, false)

                return ViewHolder(view)
            }
        }
    }

}

//Only refreshes items that changed in RecyclerView
class UpdatesDiffCallback : DiffUtil.ItemCallback<Map.Entry<String, ArrayList<String>>>() {

    override fun areItemsTheSame(oldItem: Map.Entry<String, ArrayList<String>>, newItem: Map.Entry<String, ArrayList<String>>): Boolean {
        return oldItem.key == newItem.key
    }

    override fun areContentsTheSame(oldItem: Map.Entry<String, ArrayList<String>>, newItem: Map.Entry<String, ArrayList<String>>): Boolean {
        return oldItem == newItem
    }


}
