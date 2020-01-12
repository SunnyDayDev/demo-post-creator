package dev.sunnyday.postcreator.core.activitytracker.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.core.activitytracker.ActivityTrackerObserver
import dev.sunnyday.postcreator.core.activitytracker.ActivityTracker

@Module(includes = [InternalCoreActivityTrackerModule::class])
class CoreActivityTrackerModule

@Module
internal interface InternalCoreActivityTrackerModule {

    @Binds
    fun bindActivityObserver(impl: ActivityTracker): ActivityTrackerObserver

}