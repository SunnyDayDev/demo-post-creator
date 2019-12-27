package dev.sunnyday.postcreator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.updateLayoutParams
import dev.sunnyday.postcreator.core.common.android.resolveAttribute
import dev.sunnyday.postcreator.postcreator.decorations.TextColorFillDecorator
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateCreatorSize()

        creatorView.addTextDecorator(
            TextColorFillDecorator(theme.resolveAttribute(R.attr.colorPrimary).data))
    }

    private fun updateCreatorSize() {
        val display = resources.displayMetrics
        val smallestSize = min(display.widthPixels, display.heightPixels)

        creatorView.updateLayoutParams {
            width = smallestSize
        }
    }
    
}
