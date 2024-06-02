package com.example.runtracker.gallery

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.R

/**
 * ViewHolder for displaying an image in the gallery.
 */
class GalleryViewHolder(itemView: View, private var onImageClickListener: GalleryAdapter.OnImageClickListener)
    : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var imageView: ImageView = itemView.findViewById(R.id.imageView2)

    init {
        imageView.setOnClickListener(this) // Set click listener for the image view
    }

    override fun onClick(v: View?) {
        onImageClickListener.onImageClick(adapterPosition) // Handle image click event
    }
}
