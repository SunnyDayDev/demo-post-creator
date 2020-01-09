package dev.sunnyday.postcreator.drawablechooser

import android.graphics.drawable.Drawable

data class DrawableItem(val tag: Long = 0, val source: () -> Drawable)