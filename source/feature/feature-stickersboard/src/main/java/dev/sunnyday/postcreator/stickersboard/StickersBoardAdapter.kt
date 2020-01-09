package dev.sunnyday.postcreator.stickersboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.stickersboard__item.view.*
import kotlin.properties.Delegates

internal class StickersBoardAdapter(
    val onItemClick: (StickerBoardItem, ImageView) -> Unit
) : RecyclerView.Adapter<StickersBoardAdapter.StickerViewHolder>() {

    var items: List<StickerBoardItem> by Delegates.observable(emptyList()) { _, old, new ->
        if (old == new) return@observable
        val diff = calculateDiff(old, new)
        diff.dispatchUpdatesTo(this)
    }

    private fun calculateDiff(old: List<StickerBoardItem>, new: List<StickerBoardItem>) =
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                old[oldItemPosition] == new[newItemPosition]

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
        private val onItemClick: (StickerBoardItem, ImageView) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        fun bind(sticker: StickerBoardItem) {
            val glide: RequestManager
            try {
                glide = Glide.with(view.context)
            } catch (ignore: Throwable) {
                return
            }

            glide.clear(view.image)
            glide.load(sticker.uri)
                .into(view.image)

            view.button.setOnClickListener {
                onItemClick(sticker, view.image)
            }
        }

    }

}