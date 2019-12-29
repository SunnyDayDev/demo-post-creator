package dev.sunnyday.postcreator

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.updateLayoutParams
import dev.sunnyday.postcreator.backgroundswitcher.Background
import dev.sunnyday.postcreator.backgroundswitcher.BackgroundSwitcherToolbarListener
import dev.sunnyday.postcreator.core.common.android.Dimen
import dev.sunnyday.postcreator.stickersboard.Sticker
import dev.sunnyday.postcreator.stickersboard.StickersBoard
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private val textStyleSwitcher: TextStyleSwitcher by lazy {
        TextStyleSwitcher(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateCreatorSize()

        setupTextStyleSwitcher()
        setupBackgroundSwitcher()
        setupStickers()
    }

    private fun updateCreatorSize() {
        val display = resources.displayMetrics
        val smallestSize = min(display.widthPixels, display.heightPixels)

        creatorView.updateLayoutParams {
            width = smallestSize
        }
    }

    private fun setupTextStyleSwitcher() {
        textStyleSwitcher.applyNextStyle(creatorView)

        switchTextStyleButton.setOnClickListener {
            textStyleSwitcher.applyNextStyle(creatorView)
        }
    }

    private fun setupBackgroundSwitcher() {
        backgroundSwitcher.items = listOf(
            Background.Color(Color.WHITE),
            Background.Gradient(0xFF00EFC8.toInt(), 0xFF0762E5.toInt()),
            Background.Gradient(0xFFAAE400.toInt(), 0xFF04B025.toInt()),
            Background.Gradient(0xFFFFBA00.toInt(), 0xFFFF590B.toInt()),
            Background.Gradient(0xFFFF003F.toInt(), 0xFF99004F.toInt()),
            Background.Resource(R.drawable.bg_beach),
            Background.Resource(R.drawable.bg_stars_center),
            Background.Color(Color.BLACK),
            Background.Gradient(0xFF02EFC8.toInt(), 0xFF0762E5.toInt()),
            Background.Gradient(0xFFAAE460.toInt(), 0xFF04B025.toInt()),
            Background.Gradient(0xFFFFBA00.toInt(), 0xFF27590B.toInt()),
            Background.Gradient(0xFF4F0004.toInt(), 0xFF29404F.toInt()))

        backgroundSwitcher.addListener(object : BackgroundSwitcherToolbarListener {

            override fun onBackgroundSelected(background: Background) {
                creatorView.background =
                    Background.getDrawable(this@MainActivity, background)

                if (background is Background.Color && background.color == Color.WHITE) {
                    val actionsBorderWidth = Dimen.dp(2, this@MainActivity).toInt()
                    creatorView.setActionsBorderWidth(actionsBorderWidth)
                } else {
                    creatorView.setActionsBorderWidth(0)
                }
            }

        })

        backgroundSwitcher.selectedPosition = 0
    }

    private fun setupStickers() {
        stickersButton.setOnClickListener {
            val stickers = (1L..24L).map {
                Sticker(it, Uri.parse("file:///android_asset/stickers/$it.png"))
            }

            StickersBoard.show(this, stickers) {
                creatorView.addImage(it.uri)
            }
        }
    }

}
