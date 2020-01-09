package dev.sunnyday.postcreator.domain.backgrounds.source

import android.content.Context
import android.graphics.Color
import dev.sunnyday.postcreator.core.common.android.UriUtil
import dev.sunnyday.postcreator.domain.backgrounds.Background
import dev.sunnyday.postcreator.domain.backgrounds.R
import io.reactivex.Observable
import javax.inject.Inject

internal class DefaultBackgroundsSourceImpl @Inject internal constructor(
    private val context: Context
) : DefaultBackgroundsSource {

    override fun getDefaultBackgrounds(): Observable<List<Background>> = Observable.just(listOf(
        Background.Color(
            Long.MIN_VALUE + 1,
            Color.WHITE
        ),
        Background.Gradient(
            Long.MIN_VALUE + 2,
            intArrayOf(0xFF00EFC8.toInt(), 0xFF0762E5.toInt())
        ),
        Background.Gradient(
            Long.MIN_VALUE + 3,
            intArrayOf(0xFFAAE400.toInt(), 0xFF04B025.toInt())
        ),
        Background.Gradient(
            Long.MIN_VALUE + 4,
            intArrayOf(0xFFFFBA00.toInt(), 0xFFFF590B.toInt())
        ),
        Background.Gradient(
            Long.MIN_VALUE + 5,
            intArrayOf(0xFFFF003F.toInt(), 0xFF99004F.toInt())
        ),
        Background.Resource(
            Long.MIN_VALUE + 6,
            R.drawable.backgrounds__bg__beach,
            UriUtil.resourceUri(R.drawable.backgrounds__ic__beach_thumbnail, context)
        ),
        Background.Resource(
            Long.MIN_VALUE + 7,
            R.drawable.backgrounds__bg__stars_center,
            UriUtil.resourceUri(R.drawable.backgrounds__ic__stars_thumbnail, context)
        )
    ))

}