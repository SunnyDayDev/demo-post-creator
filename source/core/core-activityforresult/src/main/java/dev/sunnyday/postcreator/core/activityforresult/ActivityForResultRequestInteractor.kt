package dev.sunnyday.postcreator.core.activityforresult

import io.reactivex.Maybe

interface ActivityForResultRequestInteractor {

    fun <T: Any> startActivityForResult(request: ActivityForResultRequest<T>): Maybe<T>

}