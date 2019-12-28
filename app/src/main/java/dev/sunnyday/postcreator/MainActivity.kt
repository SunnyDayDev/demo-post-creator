package dev.sunnyday.postcreator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var textStyleSwitcher: TextStyleSwitcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateCreatorSize()

        setupTextStyleSwitcher()
    }

    private fun updateCreatorSize() {
        val display = resources.displayMetrics
        val smallestSize = min(display.widthPixels, display.heightPixels)

        creatorView.updateLayoutParams {
            width = smallestSize
        }
    }

    private fun setupTextStyleSwitcher() {
        textStyleSwitcher = TextStyleSwitcher(creatorView)
        textStyleSwitcher.applyNextStyle()

        switchTextStyleButton.setOnClickListener {
            textStyleSwitcher.applyNextStyle()
        }
    }

}
