package dev.sunnyday.postcreator.domain.backgrounds

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Observable

interface BackgroundsRepository {

    fun backgrounds(): Observable<List<Background>>

    fun addBackground(source: Uri): Completable

}