package dev.sunnyday.postcreator.domain.stickers

import android.net.Uri
import io.reactivex.Single
import javax.inject.Inject

internal class StickersRepositoryImpl @Inject constructor(): StickersRepository {

    override fun stickers(): Single<List<Sticker>> = Single.fromCallable {
        (1L..24L).map {
            Sticker(it, Uri.parse("file:///android_asset/stickers/$it.png"))
        }
    }

}