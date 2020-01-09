package dev.sunnyday.postcreator.domain.stickers

import io.reactivex.Single

interface StickersRepository {

    fun stickers(): Single<List<Sticker>>

}