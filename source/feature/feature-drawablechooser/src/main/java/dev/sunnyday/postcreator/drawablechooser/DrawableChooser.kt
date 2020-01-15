package dev.sunnyday.postcreator.drawablechooser

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.backgroundswitcher__toolbar.view.*

class DrawableChooser @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    var items: List<DrawableItem>
        get() = adapter.items
        set(value) {
            val wasEmpty = adapter.items.isEmpty()

            val newSelectedPosition = adapter.items.getOrNull(adapter.selectedPosition)
                ?.let(value::indexOf) ?: -1

            adapter.items = value
            adapter.selectedPosition = newSelectedPosition

            if (wasEmpty) {
                list.scrollToPosition(0)
            }
        }

    var selectedPosition: Int
        get() = adapter.selectedPosition
        set(value) { internalSetSelectedPosition(value, true) }

    private fun internalSetSelectedPosition(position: Int, notifyChanged: Boolean) {
        if (adapter.selectedPosition == position) return

        adapter.selectedPosition = position

        if (notifyChanged) {
            adapter.items.getOrNull(position)
                ?.let(this::notifySelected)
        }
    }

    private val adapter = DrawableChooserAdapter(
        onItemSelected = { i, background ->
            internalSetSelectedPosition(i, false)
            notifySelected(background)
        },
        onAddClick = this::notifyAddClick)

    private var listeners = mutableSetOf<DrawableChooserListener>()

    init {

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.backgroundswitcher__toolbar, this, true)

        list.adapter = adapter

        if (isInEditMode) {
            items = listOf(
                DrawableItem { ColorDrawable(0xffff0000.toInt()) },
                DrawableItem { ColorDrawable(0xff00ff00.toInt()) },
                DrawableItem { ColorDrawable(0xff0000ff.toInt()) })
        }

    }

    fun addListener(listener: DrawableChooserListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: DrawableChooserListener) {
        listeners.remove(listener)
    }

    private fun notifySelected(item: DrawableItem) {
        listeners.forEach {
            it.onSelected(item)
        }
    }

    private fun notifyAddClick() {
        listeners.forEach(DrawableChooserListener::onAddClick)
    }

}

