package com.example.runtracker.myAccount

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.R

/**
 * Adapter for displaying user account items in a RecyclerView.
 * @param menuItem List of menu items to display.
 * @param onBlockListener Listener for block click events.
 */
class MyAccountAdapter(
    private val menuItem: ArrayList<String>,
    private var onBlockListener: OnMyAccountClickListener
) : RecyclerView.Adapter<MyAccountViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences for storing account information

    /**
     * Creates a new ViewHolder for an account item.
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new MyAccountViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_account_item, parent, false)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            parent.height / 7
        )
        view.layoutParams = layoutParams
        return MyAccountViewHolder(view, onBlockListener)
    }

    /**
     * Returns the total number of items in the dataset.
     * @return The total number of items.
     */
    override fun getItemCount(): Int {
        return menuItem.size
    }

    /**
     * Binds the data to the ViewHolder.
     * @param holder The ViewHolder.
     * @param position The position in the dataset.
     */
    override fun onBindViewHolder(holder: MyAccountViewHolder, position: Int) {
        sharedPreferences = holder.view.context.getSharedPreferences("my_account", Context.MODE_PRIVATE)

        holder.textViewTitle.text = menuItem[position] // Set the title for the menu item
        holder.textViewData.text = sharedPreferences.getString(menuItem[position], "") // Set the data for the menu item from SharedPreferences
    }

    /**
     * Interface for handling click events on account items.
     */
    interface OnMyAccountClickListener {
        fun onBlockClick(position: Int)
    }
}
