package dev.sunnyday.postcreator.core.common.android

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes

fun Resources.Theme.resolveAttribute(@AttrRes id: Int, resolveRefs: Boolean = true): TypedValue {
    val typedValue = TypedValue()
    resolveAttribute(id, typedValue, resolveRefs)
    return typedValue
}