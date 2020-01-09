package dev.sunnyday.postcreator.core.app.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.core.app.rx.AppSchedulers
import dev.sunnyday.postcreator.core.app.rx.AppSchedulersImpl

@Module(includes = [InternalAppCoreModule::class])
class CoreAppModule

@Module
internal interface InternalAppCoreModule {

    @Binds
    fun bindAppSchedulers(impl: AppSchedulersImpl): AppSchedulers

}