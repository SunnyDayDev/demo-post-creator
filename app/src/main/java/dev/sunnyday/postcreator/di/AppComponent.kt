package dev.sunnyday.postcreator.di

import android.content.Context
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dev.sunnyday.postcreator.App
import dev.sunnyday.postcreator.AppActivity
import dev.sunnyday.postcreator.core.app.dagger.PerActivity
import dev.sunnyday.postcreator.core.app.di.CoreAppModule
import dev.sunnyday.postcreator.core.permissions.di.CorePermissionsModule
import dev.sunnyday.postcreator.core.ui.di.CoreUIModule
import dev.sunnyday.postcreator.domain.backgrounds.di.DomainBackgroundsModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreUIModule::class,
        CoreAppModule::class,
        CorePermissionsModule::class,
        DomainBackgroundsModule::class,
        AppModule::class,
        AndroidInjectionModule::class
    ])
interface AppComponent: AndroidInjector<App> {

    @Component.Factory
    interface Factory: AndroidInjector.Factory<App>

}

@Module
internal interface AppModule {

    @Binds
    fun context(app: App): Context

    @PerActivity
    @ContributesAndroidInjector(modules = [AppActivityModule::class])
    fun contributeAppActivity(): AppActivity

}