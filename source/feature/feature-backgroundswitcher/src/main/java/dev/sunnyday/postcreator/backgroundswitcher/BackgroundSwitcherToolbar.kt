package dev.sunnyday.postcreator.backgroundswitcher

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import dev.sunnyday.postcreator.postcreator.R
import kotlinx.android.synthetic.main.backgroundswitcher__toolbar.view.*

class BackgroundSwitcherToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    var items: List<Background>
        get() = adapter.items
        set(value) {
            val newSelectedPosition = adapter.items.getOrNull(adapter.selectedPosition)
                ?.let(value::indexOf) ?: -1

            adapter.items = value
            adapter.selectedPosition = newSelectedPosition
        }

    var selectedPosition: Int
        get() = adapter.selectedPosition
        set(value) { internalSetSelectedPosition(value, true) }

    private fun internalSetSelectedPosition(position: Int, notifySelected: Boolean) {
        adapter.selectedPosition = position

        if (notifySelected) {
            adapter.items.getOrNull(position)
                ?.let(this::notifySelected)
        }
    }

    private val adapter = BackgroundSwitcherToolbarAdapter(
        onSelectedListener = { i, background ->
            internalSetSelectedPosition(i, false)
            notifySelected(background)
        })

    private var listeners = mutableSetOf<BackgroundSwitcherToolbarListener>()

    init {

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.backgroundswitcher__toolbar, this, true)

        list.adapter = adapter

    }

    fun addListener(listener: BackgroundSwitcherToolbarListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: BackgroundSwitcherToolbarListener) {
        listeners.remove(listener)
    }

    private fun notifySelected(background: Background) {
        listeners.forEach {
            it.onBackgroundSelected(background)
        }
    }

}

