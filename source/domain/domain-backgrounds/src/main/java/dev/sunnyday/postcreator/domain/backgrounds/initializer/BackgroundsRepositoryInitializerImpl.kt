package dev.sunnyday.postcreator.domain.backgrounds.initializer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.core.common.android.UriUtil
import dev.sunnyday.postcreator.domain.backgrounds.Background
import dev.sunnyday.postcreator.domain.backgrounds.BackgroundsMapper
import dev.sunnyday.postcreator.domain.backgrounds.R
import dev.sunnyday.postcreator.domain.backgrounds.db.BackgroundsDao
import dev.sunnyday.postcreator.domain.backgrounds.prefs.BackgroundsRepositoryPrefs
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

internal class BackgroundsRepositoryInitializerImpl @Inject internal constructor(
    private val dao: BackgroundsDao,
    private val prefs: BackgroundsRepositoryPrefs,
    private val mapper: BackgroundsMapper,
    private val appSchedulers: AppSchedulers,
    private val context: Context
) : BackgroundsRepositoryInitializer {

    @SuppressLint("CheckResult")
    override fun initialize() {
        if (prefs.isInitialized)
            return

        prefs.isInitialized = true

        initializeWithInitialBackgrounds()
            .subscribeBy(onError = {
                prefs.isInitialized = false
            })
    }

    private fun initializeWithInitialBackgrounds() = Completable
        .defer {
            val entities = getInitialBackgrounds()
                .map(mapper::plainToEntity)

            dao.insert(entities)
        }
        .subscribeOn(appSchedulers.io)

    private fun getInitialBackgrounds(): List<Background> = listOf(
        Background.Color(
            0,
            Color.WHITE
        ),
        Background.Gradient(
            0,
            intArrayOf(0xFF00EFC8.toInt(), 0xFF0762E5.toInt())
        ),
        Background.Gradient(
            0,
            intArrayOf(0xFFAAE400.toInt(), 0xFF04B025.toInt())
        ),
        Background.Gradient(
            0,
            intArrayOf(0xFFFFBA00.toInt(), 0xFFFF590B.toInt())
        ),
        Background.Gradient(
            0,
            intArrayOf(0xFFFF003F.toInt(), 0xFF99004F.toInt())
        ),
        Background.Resource(
            0,
            R.drawable.backgrounds__bg__beach,
            UriUtil.resourceUri(R.drawable.backgrounds__ic__beach_thumbnail, context)
        ),
        Background.Resource(
            0,
            R.drawable.backgrounds__bg__stars_center,
            UriUtil.resourceUri(R.drawable.backgrounds__ic__stars_thumbnail, context)
        )
    )

}