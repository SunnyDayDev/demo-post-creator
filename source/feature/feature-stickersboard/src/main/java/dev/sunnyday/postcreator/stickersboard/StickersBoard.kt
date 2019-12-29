package dev.sunnyday.postcreator.stickersboard

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.sunnyday.postcreator.core.common.android.Dimen
import kotlinx.android.synthetic.main.stickersboard__dialog_content.*
import kotlin.math.min


class StickersBoard(
    context: Context,
    private val callback: (Sticker) -> Unit
) : BottomSheetDialog(context) {

    private val adapter = StickersBoardAdapter(onItemClick = { _, sticker ->
        callback(sticker)
        dismiss()
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stickersboard__dialog_content)

        list.adapter = adapter

        // TODO: https://github.com/SunnyDayDev/demo-post-creator/issues/19
        content.maxHeight = context.resources.displayMetrics.heightPixels / 2
        behavior.halfExpandedRatio = 0.25f

        initFullWithSizing()
        initDynamicElevationTracking()
        initStickers()
    }

    private fun initFullWithSizing() {
        // TODO: https://github.com/SunnyDayDev/demo-post-creator/issues/18
        setOnShowListener {
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun initStickers() {
        adapter.items = (1L..24L).map(::Sticker)
    }

    private fun initDynamicElevationTracking() {
        val maxOffset = Dimen.dp(16, context)
        val maxElevation = Dimen.dp(2, context)

        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val offset = list.computeVerticalScrollOffset()
                val ratio = min(offset / maxOffset, 1f)
                title.elevation = ratio * maxElevation
            }
        })
    }

    companion object {

        fun show(context: Context, callback: (Sticker) -> Unit) {
            val board = StickersBoard(context, callback)
            board.show()
        }

    }

}