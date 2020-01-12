package dev.sunnyday.postcreator

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dev.sunnyday.postcreator.core.activitytracker.ActivityTrackerStarter
import dev.sunnyday.postcreator.di.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject

class App : DaggerApplication() {

    @Inject
    lateinit var activityTrackerStarter: ActivityTrackerStarter

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.factory().create(this)

    override fun onCreate() {
        super.onCreate()

        initializeApp()
        initThirdPartyLibraries()

    }

    private fun initializeApp() {
        activityTrackerStarter.start(this)
    }

    private fun initThirdPartyLibraries() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}