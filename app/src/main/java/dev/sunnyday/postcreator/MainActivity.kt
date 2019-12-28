package dev.sunnyday.postcreator

import android.graphics.Color
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.updateLayoutParams
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.core.common.android.resolveAttribute
import dev.sunnyday.postcreator.postcreator.decorations.RoundedColorFillDecorator
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateCreatorSize()
        setupTextDecorator()
        creatorView.textColor = Color.WHITE
    }

    private fun updateCreatorSize() {
        val display = resources.displayMetrics
        val smallestSize = min(display.widthPixels, display.heightPixels)

        creatorView.updateLayoutParams {
            width = smallestSize
        }
    }

    private fun setupTextDecorator() {
        val decoratorPadding = RectF(
            Dimen.dp(4, this),
            Dimen.dp(8, this),
            Dimen.dp(4, this),
            Dimen.dp(0, this))

        val decorator = RoundedColorFillDecorator(
            color = theme.resolveAttribute(R.attr.colorPrimary).data,
            radius = Dimen.dp(4, this),
            padding = decoratorPadding)

        creatorView.addTextDecorator(decorator)
    }
    
}
