package dev.sunnyday.postcreator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateCreatorSize()
    }

    private fun updateCreatorSize() {
        val display = resources.displayMetrics
        val smallestSize = min(display.widthPixels, display.heightPixels)

        creatorView.updateLayoutParams {
            width = smallestSize
        }
    }
    
}
