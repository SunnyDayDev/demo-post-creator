package dev.sunnyday.postcreator.domain.backgrounds.resolver

import android.graphics.drawable.Drawable
import dev.sunnyday.postcreator.domain.backgrounds.Background

interface BackgroundResolver {

    fun resolveIcon(background: Background): Drawable?

    fun resolve(background: Background): Drawable?

}