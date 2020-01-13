package dev.sunnyday.postcreator.domain.backgrounds

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.domain.backgrounds.db.BackgroundEntity
import dev.sunnyday.postcreator.domain.backgrounds.db.BackgroundEntityType
import dev.sunnyday.postcreator.domain.backgrounds.db.BackgroundsDao
import dev.sunnyday.postcreator.domain.backgrounds.source.DefaultBackgroundsSource
import dev.sunnyday.postcreator.core.common.android.InputStreamUtil
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject

internal class BackgroundsRepositoryImpl @Inject constructor(
    private val dao: BackgroundsDao,
    private val mapper: BackgroundsMapper,
    private val defaultBackgroundsSource: DefaultBackgroundsSource,
    private val schedulers: AppSchedulers,
    private val context: Context
) : BackgroundsRepository {

    override fun backgrounds(): Observable<List<Background>> {
        val default = defaultBackgroundsSource.getDefaultBackgrounds()

        val stored = dao.items
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.background)
            .map { it.map(mapper::entityToPlain) }

        return Observables.combineLatest(default, stored) { d, s -> d + s }
    }

    override fun addBackground(source: Uri): Completable =
        getBackgroundBitmap(source)
            .flatMap(this::saveBackgroundInInternalStorage)
            .flatMapCompletable(this::storeBackgroundEntity)
            .subscribeOn(schedulers.io)

    private fun getBackgroundBitmap(source: Uri): Single<Bitmap> = Single.fromCallable {
        val stream = InputStreamUtil.inputStreamFromUri(source, context)!!
        BitmapFactory.decodeStream(stream)
    }

    private fun saveBackgroundInInternalStorage(
        bitmap: Bitmap
    ): Single<SavedBackground> = Single.fromCallable {
        val iconBitmap = Bitmap.createScaledBitmap(bitmap, 92, 92, true)

        val dir = context.getExternalFilesDir("backgrounds")!!
        if (!dir.exists() && !dir.mkdirs()) {
            throw IOException("Can't create folder: $dir")
        }

        val uuid = UUID.randomUUID()

        val sourceFile = File(dir, "$uuid.jpg")
        FileOutputStream(sourceFile).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        val iconFile = File(dir, "$uuid-icon.jpg")
        FileOutputStream(iconFile).use {
            iconBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        SavedBackground(
            iconSource = Uri.fromFile(iconFile),
            source = Uri.fromFile(sourceFile))
    }

    private fun storeBackgroundEntity(background: SavedBackground): Completable {
        val backgroundEntity = BackgroundEntity(
            id = 0,
            type = BackgroundEntityType.STORED,
            value = background.source.toString(),
            icon = background.iconSource.toString())

        return dao.insert(backgroundEntity)
    }

    private data class SavedBackground(val iconSource: Uri, val source: Uri)

}