package dev.sunnyday.postcreator.core.activityforresult

import io.reactivex.Completable
import io.reactivex.Maybe

interface ActivityRequestInteractor {

    fun <T: Any> startActivityForResult(request: ActivityRequest<T>): Maybe<T>

    fun startActivity(request: ActivityRequest<*>): Completable

}