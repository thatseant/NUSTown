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
        holder.bind(item, holder, newListener) //bind function in ViewHolder sets Views within it
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

        fun bind(item: Map.Entry<String, ArrayList<String>>, holder: ViewHolder, listener: OnItemSelectedListener) {
//            postDate.text = item.key
            postCaption.text = item.value.get(0)
            editButton.visibility = VISIBLE
            editButton.setOnClickListener { view ->
                listener.onItemSelected(item, view)
            }
            deleteButton.visibility = VISIBLE
            deleteButton.setOnClickListener { view ->
                listener.deleteItemSelected(item, view)
            }
            //Sets ImageView
//            Glide.with(holder.postImage.context).load(item.value.get(1))
//                    .thumbnail(0.02f).into(postImage)

            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("updates/" + item.value.get(1) + ".jpg")

            imageRef.downloadUrl.addOnSuccessListener {
                Glide.with(holder.postImage.context).load(it).into(postImage)
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
