package dev.sunnyday.postcreator.domain.backgrounds

import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.domain.backgrounds.db.BackgroundsDao
import io.reactivex.Observable
import javax.inject.Inject

internal class BackgroundsRepositoryImpl @Inject constructor(
    private val dao: BackgroundsDao,
    private val mapper: BackgroundsMapper,
    private val schedulers: AppSchedulers
) : BackgroundsRepository {

    override fun backgrounds(): Observable<List<Background>> =
        dao.items
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.background)
            .map { it.map(mapper::entityToPlain) }

}