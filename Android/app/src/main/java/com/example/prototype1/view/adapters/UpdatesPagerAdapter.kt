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
import com.example.prototype1.R

class UpdatesPagerAdapter(mListener: OnItemSelectedListener) : ListAdapter<Map.Entry<String, ArrayList<String>>, UpdatesPagerAdapter.ViewHolder>(UpdatesDiffCallback()) {
    private val newListener: OnItemSelectedListener = mListener


    interface OnItemSelectedListener {
        fun onItemSelected(MPost: Map.Entry<String, ArrayList<String>>, view: View)
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
        private val postImage: ImageView = itemView.findViewById(R.id.postImage)

        fun bind(item: Map.Entry<String, ArrayList<String>>, holder: ViewHolder, listener: OnItemSelectedListener) {
//            postDate.text = item.key
            postCaption.text = item.value.get(0)

            //Sets ImageView
//            if (item.imgUrl != "") {
            Glide.with(holder.postImage.context).load(item.value.get(1))
                    .thumbnail(0.02f).into(postImage)
//            } else {
//                clubImage.setImageResource(R.drawable.nus)
//            }

            itemView.setOnClickListener { view ->
                listener.onItemSelected(item, view)
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