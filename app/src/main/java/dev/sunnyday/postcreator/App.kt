package dev.sunnyday.postcreator

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dev.sunnyday.postcreator.core.ui.ActivityTracker
import dev.sunnyday.postcreator.di.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject

class App : DaggerApplication() {

    @Inject
    internal lateinit var activityTracker: ActivityTracker

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.factory().create(this)

    override fun onCreate() {
        super.onCreate()

        initThirdPartyLibraries()

        registerActivityLifecycleCallbacks(activityTracker)
    }

    private fun initThirdPartyLibraries() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}