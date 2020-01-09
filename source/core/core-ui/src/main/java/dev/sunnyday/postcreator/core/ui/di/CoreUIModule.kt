package dev.sunnyday.postcreator.core.ui.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.core.ui.ActivityObserver
import dev.sunnyday.postcreator.core.ui.ActivityTracker
import dev.sunnyday.postcreator.core.ui.ActivityTrackerImpl

@Module(includes = [CoreUIBindsModule::class])
abstract class CoreUIModule

@Module
internal interface CoreUIBindsModule {

    @Binds
    fun bindActivityTracker(impl: ActivityTrackerImpl): ActivityTracker

    @Binds
    fun bindActivityObserver(impl: ActivityTrackerImpl): ActivityObserver

}