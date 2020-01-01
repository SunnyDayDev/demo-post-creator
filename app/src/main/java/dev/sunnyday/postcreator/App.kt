package dev.sunnyday.postcreator

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dev.sunnyday.postcreator.core.ui.ActivityTracker
import dev.sunnyday.postcreator.di.DaggerAppComponent
import dev.sunnyday.postcreator.domain.backgrounds.initializer.BackgroundsRepositoryInitializer
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class App : DaggerApplication() {

    @Inject
    internal lateinit var activityTracker: ActivityTracker

    @Inject
    internal lateinit var backgroundsInitializer: Provider<BackgroundsRepositoryInitializer>

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.factory().create(this)

    override fun onCreate() {
        super.onCreate()

        initializeApp()
        initThirdPartyLibraries()

        registerActivityLifecycleCallbacks(activityTracker)
    }

    private fun initializeApp() {
        backgroundsInitializer.get().initialize()
    }

    private fun initThirdPartyLibraries() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}