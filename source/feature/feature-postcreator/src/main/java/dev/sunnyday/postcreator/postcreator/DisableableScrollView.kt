package dev.sunnyday.postcreator.postcreator

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

internal class DisableableScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
) : ScrollView(context, attrs, defStyle) {

    var isScrollEnabled = true

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean = when {
        isScrollEnabled -> super.dispatchTouchEvent(ev)
        childCount != 0 -> {
            ev.offsetLocation(0f, scrollY.toFloat())
            getChildAt(0).dispatchTouchEvent(ev)
        }
        else -> false
    }

}