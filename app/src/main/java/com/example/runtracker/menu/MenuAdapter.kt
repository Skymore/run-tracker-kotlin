package com.example.runtracker.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.R

/**
 * Adapter for displaying menu items in a RecyclerView.
 * @param menuItems List of menu items to display.
 * @param onBlockListener Listener for block click events.
 */
class MenuAdapter(
    private var menuItems: ArrayList<MenuItem>,
    private var onBlockListener: OnBlockClickListener
) : RecyclerView.Adapter<MenuViewHolder>() {

    /**
     * Creates a new ViewHolder for a menu item.
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new MenuViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return MenuViewHolder(view, onBlockListener)
    }

    /**
     * Returns the total number of items in the dataset.
     * @return The total number of items.
     */
    override fun getItemCount(): Int {
        return menuItems.size
    }

    /**
     * Binds the data to the ViewHolder.
     * @param holder The ViewHolder.
     * @param position The position in the dataset.
     */
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.textView.text = menuItems[position].data // Set the text for the menu item
        holder.imageView.setImageResource(menuItems[position].drawable) // Set the image for the menu item
    }

    /**
     * Interface for handling click events on menu items.
     */
    interface OnBlockClickListener {
        fun onBlockClick(position: Int, date: String)
    }
}
