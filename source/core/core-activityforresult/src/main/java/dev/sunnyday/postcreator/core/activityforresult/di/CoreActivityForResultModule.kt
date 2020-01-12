package dev.sunnyday.postcreator.core.activityforresult.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.core.activityforresult.ActivityRequestInteractor
import dev.sunnyday.postcreator.core.activityforresult.ActivityRequestInteractorImpl

@Module(includes = [InternalCoreActivityForResultModule::class])
interface CoreActivityForResultModule

@Module
internal interface InternalCoreActivityForResultModule {

    @Binds
    fun bindActivityForResultInteractor(impl: ActivityRequestInteractorImpl): ActivityRequestInteractor

}