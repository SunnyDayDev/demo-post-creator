package dev.sunnyday.postcreator.stickersboard

import android.animation.RectEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.os.postDelayed
import androidx.core.view.*
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.sunnyday.postcreator.core.common.android.Dimen
import kotlinx.android.synthetic.main.stickersboard__dialog_content.*
import kotlin.math.min


class StickersBoard private constructor(
    context: Context,
    private val stickers: List<StickerBoardItem>,
    private val targetRectProvider: (() -> Rect)? = null,
    private val callback: (StickerBoardItem) -> Unit
) : BottomSheetDialog(context) {

    private val adapter = StickersBoardAdapter(onItemClick = this::onStickerSelected)

    private var isAnimatingSelection = false
    private var isShouldDelayDismiss = false
    private var isDismissed = false

    private val uiHandler = Handler(Looper.getMainLooper())

    private tailrec fun findParent(view: View, check: (ViewGroup) -> Boolean): ViewGroup? {
        val parent = view.parent as? ViewGroup ?: return null
        return if (check(parent)) {
            parent
        } else {
            findParent(parent, check)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stickersboard__dialog_content)

        list.adapter = adapter
        adapter.items = stickers

        content.maxHeight = context.resources.displayMetrics.heightPixels / 2
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        initFullWidthSize()
        initDynamicTitleElevation()
    }

    private fun initFullWidthSize() {
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun initDynamicTitleElevation() {
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (isAnimatingSelection) {
            true
        } else {
            super.dispatchTouchEvent(ev)
        }
    }

    private fun onStickerSelected(sticker: StickerBoardItem, stickerView: ImageView) {
        val contentRoot = findParent(stickerView) { it.id == android.R.id.content }

        if (contentRoot == null || targetRectProvider == null) {
            callback(sticker)
            dismiss()
            return
        }

        isAnimatingSelection = true
        isShouldDelayDismiss = true

        val rect = Rect()
        stickerView.getGlobalVisibleRect(rect)

        (stickerView.parent as ViewGroup).removeView(stickerView)
        (contentRoot[0] as ViewGroup).addView(stickerView)

        val targetRect = targetRectProvider.invoke()
        val evaluator = RectEvaluator(Rect())

        ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = FastOutSlowInInterpolator()

            addUpdateListener {
                val value = it.animatedValue as Float
                val valueRect = evaluator.evaluate(value, rect, targetRect)

                val tintColor = (0x99 * it.animatedFraction).toInt() shl 24
                stickerView.imageTintList = ColorStateList.valueOf(tintColor)

                stickerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    width = valueRect.width()
                    height = valueRect.height()

                    topMargin = valueRect.top
                    leftMargin = valueRect.left
                }
            }

            doOnEnd {
                callback(sticker)
                uiHandler.post {
                    stickerView.isVisible = false

                    isAnimatingSelection = false
                    dismiss()
                }
            }

            start()
        }
    }

    override fun dismiss() {
        if (isActivityStopped()) {
            super.dismiss()
            return
        }

        if (isDismissed || isAnimatingSelection) return
        isDismissed = true

        if (isShouldDelayDismiss) {
            uiHandler.postDelayed(50) {
                super.dismiss()
            }
        } else {
            super.dismiss()
        }
    }

    private fun isActivityStopped(): Boolean {
        val lifecycle = (ownerActivity as? LifecycleOwner)?.lifecycle
            ?: return false

        return !lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    companion object {

        fun show(
            context: Context,
            stickers: List<StickerBoardItem>,
            stickerRectProvider: (() -> Rect)? = null,
            callback: (StickerBoardItem) -> Unit
        ) = StickersBoard(context, stickers, stickerRectProvider, callback)
            .also(StickersBoard::show)

    }

}