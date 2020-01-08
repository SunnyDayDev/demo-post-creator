package dev.sunnyday.postcreator.postcreatorboard.touchtracker

import android.graphics.PointF
import dev.sunnyday.postcreator.postcreatorboard.PostCreatorImage

internal class ImageTouchTrackerFactory {

    fun create(
        image: PostCreatorImage,
        onMoved: (PointF, PostCreatorImage) -> Unit,
        onComplete: (PointF, PostCreatorImage) -> Unit
    ): TouchTracker = ImageTouchTracker(image, onMoved, onComplete)

}