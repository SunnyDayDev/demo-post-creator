package dev.sunnyday.postcreator.core.activityforresult.di

import dagger.Binds
import dagger.Module
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequestInteractor
import dev.sunnyday.postcreator.core.activityforresult.ActivityForResultRequestInteractorImpl

@Module(includes = [InternalCoreActivityForResultModule::class])
interface CoreActivityForResultModule

@Module
internal interface InternalCoreActivityForResultModule {

    @Binds
    fun bindPermissionsInteractor(impl: ActivityForResultRequestInteractorImpl): ActivityForResultRequestInteractor

}