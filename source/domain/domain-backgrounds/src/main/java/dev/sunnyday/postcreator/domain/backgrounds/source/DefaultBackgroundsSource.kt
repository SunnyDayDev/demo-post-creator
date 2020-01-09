package dev.sunnyday.postcreator.domain.backgrounds.source

import dev.sunnyday.postcreator.domain.backgrounds.Background
import io.reactivex.Observable

interface DefaultBackgroundsSource {

    fun getDefaultBackgrounds(): Observable<List<Background>>

}