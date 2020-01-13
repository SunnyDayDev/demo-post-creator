package dev.sunnyday.postcreator.postcreatorboard.touchtracker

import android.graphics.PointF
import dev.sunnyday.postcreator.postcreatorboard.PostCreatorImage

internal class ImageTouchTrackerFactory {

    fun create(
        image: PostCreatorImage,
        onClick: (PointF, PostCreatorImage) -> Unit,
        onStartMove: (PointF, PostCreatorImage) -> Unit,
        onMove: (PointF, PostCreatorImage) -> Unit,
        onCompleteMove: (PointF, PostCreatorImage) -> Unit
    ): TouchTracker = ImageTouchTracker(image, onClick, onStartMove, onMove, onCompleteMove)

}