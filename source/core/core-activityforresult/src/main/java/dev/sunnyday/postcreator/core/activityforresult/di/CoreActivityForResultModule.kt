package dev.sunnyday.postcreator.core.activityforresult.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequestInteractor
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequestInteractorActivityObserver
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequestInteractorActivityTracker
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequestInteractorImpl

@Module(includes = [InternalCoreActivityForResultModule::class])
interface CoreActivityForResultModule

@Module
internal interface InternalCoreActivityForResultModule {

    @Binds
    fun bindActivityForResultInteractor(impl: ActivityForResultRequestInteractorImpl): ActivityForResultRequestInteractor

    @Binds
    fun bindActivityForResultInteractorActivityObserver(impl: ActivityForResultRequestInteractorActivityTracker): ActivityForResultRequestInteractorActivityObserver

}