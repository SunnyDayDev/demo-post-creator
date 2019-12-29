package dev.sunnyday.postcreator.stickersboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.stickersboard__item.view.*
import kotlin.properties.Delegates

internal class StickersBoardAdapter(
    val onItemClick: (Int, Sticker) -> Unit
) : RecyclerView.Adapter<StickersBoardAdapter.StickerViewHolder>() {

    var items: List<Sticker> by Delegates.observable(emptyList()) { _, old, new ->
        if (old == new) return@observable
        val diff = calculateDiff(old, new)
        diff.dispatchUpdatesTo(this)
    }

    private fun calculateDiff(old: List<Sticker>, new: List<Sticker>) =
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                old[oldItemPosition].id == new[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                old[oldItemPosition] == new[newItemPosition]

        })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.stickersboard__item, parent, false)
        return StickerViewHolder(view, onItemClick)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class StickerViewHolder(
        private val view: View,
        private val onItemClick: (Int, Sticker) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        fun bind(sticker: Sticker) {
            val glide = Glide.with(view.context)

            glide.clear(view.image)
            glide.load(sticker.uri)
                .into(view.image)

            view.button.setOnClickListener {
                onItemClick(adapterPosition, sticker)
            }
        }

    }

}