package dev.sunnyday.postcreator

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequestInteractorStarter
import dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractorStarter
import dev.sunnyday.postcreator.di.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class App : DaggerApplication() {

    @Inject
    internal lateinit var activityForResultInteractorStarter: Provider<ActivityForResultRequestInteractorStarter>

    @Inject
    internal lateinit var permissionInteractorStarter: Provider<PermissionRequestInteractorStarter>

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.factory().create(this)

    override fun onCreate() {
        super.onCreate()

        initializeApp()
        initThirdPartyLibraries()

    }

    private fun initializeApp() {
        activityForResultInteractorStarter.get().start(this)
        permissionInteractorStarter.get().start(this)
    }

    private fun initThirdPartyLibraries() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}