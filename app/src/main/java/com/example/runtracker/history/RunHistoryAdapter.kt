package com.example.runtracker.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.R
import com.example.runtracker.statistics.StringFormatter

/**
 * Adapter for displaying run history items in a RecyclerView.
 */
class RunHistoryAdapter(
    private val runItems: ArrayList<RunHistoryItem>,
    private val onRunItemClickListener: OnRunItemClickListener
) : RecyclerView.Adapter<RunHistoryItemViewHolder>() {

    /**
     * Creates a new ViewHolder for a run item.
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new RunHistoryItemViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunHistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.run_item, parent, false)
        return RunHistoryItemViewHolder(view, onRunItemClickListener)
    }

    /**
     * Binds the data to the ViewHolder.
     * @param holder The ViewHolder.
     * @param position The position in the dataset.
     */
    override fun onBindViewHolder(holder: RunHistoryItemViewHolder, position: Int) {
        val currentItem = runItems[position]
        holder.runID = currentItem.getRunID()
        holder.textViewName.text = currentItem.getName()
        holder.textViewDistance.text = StringFormatter.getInstance().formatDistance(currentItem.distance)
        holder.textViewDuration.text = StringFormatter.getInstance().formatTime(currentItem.duration)
    }

    /**
     * Returns the total number of items in the dataset.
     * @return The total number of items.
     */
    override fun getItemCount(): Int {
        return runItems.size
    }

    /**
     * Interface for handling click events on run items.
     */
    interface OnRunItemClickListener {
        fun onClick(position: Int, runID: Int)
    }
}
