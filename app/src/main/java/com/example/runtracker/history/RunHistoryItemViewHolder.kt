package com.example.runtracker.history

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.R
import com.example.runtracker.history.RunHistoryAdapter.OnRunItemClickListener

/**
 * ViewHolder for displaying run history items in a RecyclerView.
 * @param itemView The view of the item.
 * @param onRunItemClickListener Listener for item click events.
 */
class RunHistoryItemViewHolder(
    itemView: View,
    private val onRunItemClickListener: OnRunItemClickListener
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var textViewName: TextView = itemView.findViewById(R.id.textViewRunItemName) // TextView for run name
    var textViewDistance: TextView = itemView.findViewById(R.id.textViewRunItemDistance) // TextView for run distance
    var textViewDuration: TextView = itemView.findViewById(R.id.textViewRunItemDuration) // TextView for run duration
    var runID = 0 // ID of the run

    init {
        // Set click listener for the entire item view
        itemView.setOnClickListener(this)
    }

    /**
     * Handles click events on the item view.
     * @param view The view that was clicked.
     */
    override fun onClick(view: View) {
        onRunItemClickListener.onClick(adapterPosition, runID)
    }
}
