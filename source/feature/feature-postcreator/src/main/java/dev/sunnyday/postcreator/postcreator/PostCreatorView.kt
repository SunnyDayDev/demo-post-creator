package dev.sunnyday.postcreator.postcreator

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout

class PostCreatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.postcreator__view, this, true)
    }

}