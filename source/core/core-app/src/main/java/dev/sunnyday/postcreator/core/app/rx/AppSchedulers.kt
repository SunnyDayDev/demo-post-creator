package dev.sunnyday.postcreator.core.app.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface AppSchedulers {

    val ui: Scheduler

    val io: Scheduler

    val background: Scheduler

}

@Singleton
internal class AppSchedulersImpl @Inject constructor() : AppSchedulers {

    override val ui: Scheduler get() = AndroidSchedulers.mainThread()

    override val io: Scheduler get() = Schedulers.io()

    override val background: Scheduler get() = Schedulers.computation()

}