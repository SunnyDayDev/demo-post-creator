package dev.sunnyday.postcreator.postcreator

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import dev.sunnyday.postcreator.postcreator.decorations.TextDecorator

class PostCreatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?  = null, defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val textDecorators = mutableSetOf<TextDecorator>()

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.postcreator__view, this, true)
    }

    fun addTextDecorator(decorator: TextDecorator) {
        textDecorators.add(decorator)
    }

    fun removeTextDecorator(decorator: TextDecorator) {
        textDecorators.remove(decorator)
    }

}