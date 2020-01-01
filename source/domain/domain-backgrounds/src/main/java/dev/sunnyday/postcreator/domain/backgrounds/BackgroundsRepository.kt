package dev.sunnyday.postcreator.domain.backgrounds

import io.reactivex.Observable

interface BackgroundsRepository {

    fun backgrounds(): Observable<List<Background>>

}