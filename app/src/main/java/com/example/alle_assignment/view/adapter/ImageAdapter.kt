package com.example.alle_assignment.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alle_assignment.R

class ImageAdapter(private val images: List<String>, private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    var selectedItemPosition = 0
        set(value) {
            val previousPosition = field
            field = value
            notifyItemChanged(previousPosition)
            notifyItemChanged(value)
        }
    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewThumbnail)
        val layout: View = view.findViewById(R.id.itemLayout) // Assuming you have a layout ID

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.thumbnail_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val reversedPosition = images.size - 1 - position
        val imagePath = images[reversedPosition]
        holder.layout.isSelected = position == selectedItemPosition

        Glide.with(holder.imageView.context)
            .load(imagePath)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            selectedItemPosition = position
            notifyDataSetChanged() // Notify to refresh the item states
            onClick(imagePath)
        }
    }

    override fun getItemCount() = images.size
}
