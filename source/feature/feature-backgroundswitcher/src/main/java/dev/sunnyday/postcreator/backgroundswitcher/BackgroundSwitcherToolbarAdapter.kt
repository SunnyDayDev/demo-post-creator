package dev.sunnyday.postcreator.backgroundswitcher

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.backgroundswitcher__toolbar_item.view.*
import kotlin.properties.Delegates

internal class BackgroundSwitcherToolbarAdapter(
    private val onSelectedListener: (Int, Background) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<Background> = emptyList()
        set(value) {
            if (field === value)
                return

            val diff = calculateDiff(field, value)
            field = value
            diff.dispatchUpdatesTo(this)
        }

    var selectedPosition: Int by Delegates.observable(-1) { _, old, new ->
        if (old == new)
            return@observable

        if (new in items.indices) {
            notifyItemChanged(new)
        }
        if (old in items.indices) {
            notifyItemChanged(old)
        }
    }

    private fun calculateDiff(old: List<Background>, new: List<Background>): DiffUtil.DiffResult =
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

        val layoutId =
            if (viewType == TYPE_ITEM) R.layout.backgroundswitcher__toolbar_item
            else R.layout.backgroundswitcher__toolbar_selected_item
        val itemView = inflater.inflate(layoutId, parent, false)
        return BackgroundItemViewHolder(itemView, onSelectedListener)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = when (position) {
        selectedPosition -> TYPE_SELECTED_ITEM
        else -> TYPE_ITEM
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) = when (holder) {
        is BackgroundItemViewHolder -> holder.bind(items[position])
        else -> {
        }
    }

    private class BackgroundItemViewHolder(
        private val view: View,
        private val onSelectedListener: (Int, Background) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        fun bind(background: Background) {
            val drawable = if (background is Background.Color && background.color == Color.WHITE) {
                ColorDrawable(0x1E000000)
            } else {
                Background.getDrawable(view.context, background)
            }

            view.backgroundImage.setImageDrawable(drawable)

            view.button.setOnClickListener {
                onSelectedListener(adapterPosition, background)
            }
        }

    }

    private companion object {

        private const val TYPE_ITEM = 0
        private const val TYPE_SELECTED_ITEM = 1

    }

}