package dev.sunnyday.postcreator.stickersboard

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlin.properties.Delegates

class MaxHeightLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    var maxHeight: Int by Delegates.observable(Int.MAX_VALUE) { _, _, _ ->
        invalidate()
    }

    init {
        val a: TypedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.MaxHeightLinearLayout, 0, 0)

        maxHeight = try {
            a.getInteger(R.styleable.MaxHeightLinearLayout_maxHeight, 0)
        } finally {
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST))
    }

}