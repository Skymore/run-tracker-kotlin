package com.example.runtracker.menu

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.R

/**
 * ViewHolder for displaying a menu item in a RecyclerView.
 * @param view The view of the item.
 * @param onBlockListener Listener for block click events.
 */
class MenuViewHolder(
    val view: View,
    private var onBlockListener: MenuAdapter.OnBlockClickListener
) : RecyclerView.ViewHolder(view), View.OnClickListener {

    var textView: TextView = view.findViewById(R.id.text) // TextView for displaying the menu item text
    var imageView: ImageView = view.findViewById(R.id.image) // ImageView for displaying the menu item image

    init {
        // Set click listener for the entire item view
        view.setOnClickListener(this)
    }

    /**
     * Handles click events on the item view.
     * @param v The view that was clicked.
     */
    override fun onClick(v: View?) {
        onBlockListener.onBlockClick(adapterPosition, "") // Notify the listener of the click event
    }
}
