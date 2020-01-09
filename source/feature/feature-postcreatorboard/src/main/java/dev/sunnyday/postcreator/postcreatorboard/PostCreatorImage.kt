package dev.sunnyday.postcreator.postcreatorboard

import android.graphics.Rect
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PostCreatorImage internal constructor(
    internal val id: UUID,
    val source: Uri,
    val rect: Rect,
    var rotation: Float
) : Parcelable {

    constructor(
        source: Uri,
        rect: Rect = Rect(),
        rotation: Float = 0f
    ): this(UUID.randomUUID(), source, rect, rotation)

}