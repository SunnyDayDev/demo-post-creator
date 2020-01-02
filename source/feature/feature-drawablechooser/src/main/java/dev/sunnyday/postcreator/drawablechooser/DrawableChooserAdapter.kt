package dev.sunnyday.postcreator.drawablechooser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.backgroundswitcher__toolbar_item.view.*

internal class DrawableChooserAdapter(
    private val onItemSelected: (Int, DrawableItem) -> Unit,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<DrawableItem> = emptyList()
        set(value) {
            if (field === value)
                return

            val diff = calculateDiff(field, value)
            field = value
            diff.dispatchUpdatesTo(this)
        }

    var selectedPosition: Int = -1
        set(value) {
            val isValid = value in items.indices
            val checkedValue = if (isValid) value else -1

            if (field == checkedValue)
                return

            val previous = field

            field = checkedValue

            if (isValid) {
                notifyItemChanged(checkedValue)
            }
            if (previous in items.indices) {
                notifyItemChanged(previous)
            }
        }

    private fun calculateDiff(old: List<DrawableItem>, new: List<DrawableItem>): DiffUtil.DiffResult =
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean = old[oldItemPosition] == new[newItemPosition]

            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean = old[oldItemPosition] == new[newItemPosition]

        })

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_ADD_ACTION -> {
                val itemView = inflater.inflate(
                    R.layout.backgroundswitcher__toolbar_add_item, parent, false)
                ActionItemViewHolder(itemView)
            }
            else -> {
                val layoutId =
                    if (viewType == TYPE_ITEM) R.layout.backgroundswitcher__toolbar_item
                    else R.layout.backgroundswitcher__toolbar_selected_item
                val itemView = inflater.inflate(layoutId, parent, false)
                BackgroundItemViewHolder(itemView, onItemSelected)
            }
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun getItemViewType(position: Int): Int = when (position) {
        itemCount - 1 -> TYPE_ADD_ACTION
        selectedPosition -> TYPE_SELECTED_ITEM
        else -> TYPE_ITEM
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) = when (holder) {
        is BackgroundItemViewHolder -> holder.bind(items[position])
        is ActionItemViewHolder -> holder.bind(onAddClick)
        else -> { }
    }

    private class ActionItemViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(onClick: () -> Unit) {
            view.button.setOnClickListener { onClick() }
        }

    }

    private class BackgroundItemViewHolder(
        private val view: View,
        private val onItemSelected: (Int, DrawableItem) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        fun bind(item: DrawableItem) {
            view.backgroundImage.setImageDrawable(item.source())

            view.button.setOnClickListener {
                onItemSelected(adapterPosition, item)
            }
        }

    }

    private companion object {

        private const val TYPE_ITEM = 0
        private const val TYPE_SELECTED_ITEM = 1
        private const val TYPE_ADD_ACTION = 2

    }

}